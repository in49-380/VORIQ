package com.voriq.security_service.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voriq.security_service.exception_handler.exception.RateLimitExceededException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.voriq.security_service.config.SecurityConfig.ISSUE_URL;

/**
 * Per-user rate limiting filter for the token issue endpoint.
 *
 * <p>Identifies a user by {@code userId} in the request body (JSON), attaches it as a request attribute
 * {@link #ATTR_USER_ID}, and enforces a minimal interval between consecutive requests from the same user.
 * If the interval has not elapsed, throws {@link RateLimitExceededException} that is handled by the
 * configured {@link HandlerExceptionResolver} and rendered as HTTP 429.</p>
 *
 * <h3>Scope</h3>
 * <ul>
 *   <li>Applies only to {@code POST} requests at {@code ISSUE_URL} (with or without a global {@code /api} prefix).</li>
 *   <li>Uses an atomic {@link ConcurrentHashMap#compute(Object, java.util.function.BiFunction)} to prevent races.</li>
 *   <li>Wraps the request with {@link RepeatableBodyRequestWrapper} so downstream can re-read the body.</li>
 * </ul>
 *
 * <h3>Retry-After</h3>
 * <p>When rate-limited, the exception includes the recommended retry delay (seconds),
 * computed from {@code rate.limit}.</p>
 *
 * @author RsLan
 * @since 1.0.0
 */
@Component
public class TokenRateLimitFilter extends OncePerRequestFilter {

    /**
     * Minimal interval between requests from the same user (milliseconds).
     */
    @Value("${rate.limit}")
    private long requestLimitIntervalMs;

    /**
     * Request attribute name used to propagate resolved user id to downstream components (e.g., logging).
     */
    public static final String ATTR_USER_ID = "X_USER_ID";

    private final Map<String, Long> userRequestTimestamps = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HandlerExceptionResolver exceptionResolver;

    public TokenRateLimitFilter(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
    }

    /**
     * Applies rate limiting for POST {@code ISSUE_URL}. If the request is not subject to the filter,
     * it is passed through unchanged.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        HttpServletRequest toNext = request;

        try {
            String requestURI = request.getRequestURI();

            // Apply only to POST /v1/tokens/issue (with or without "/api" context prefix).
            boolean matchesPath = (("/api" + ISSUE_URL).equals(requestURI) || ISSUE_URL.equals(requestURI));
            boolean isPost = "POST".equalsIgnoreCase(request.getMethod());
            if (matchesPath && isPost) {

                byte[] bodyBytes = request.getInputStream().readAllBytes();
                String body = new String(bodyBytes, StandardCharsets.UTF_8);

                // Wrap first so we can attach attributes to the wrapper passed downstream.
                toNext = new RepeatableBodyRequestWrapper(request, bodyBytes);

                String userId = extractUserIdFromJson(body);
                if (userId != null && !userId.isBlank()) {
                    // Attach attribute to the WRAPPER for consistency downstream
                    toNext.setAttribute(ATTR_USER_ID, userId);

                    // Atomic check-and-update to avoid races for the same userId
                    userRequestTimestamps.compute(userId, (k, last) -> {
                        long now = System.currentTimeMillis();
                        if (last != null && now - last < requestLimitIntervalMs) {
                            long retryMs = requestLimitIntervalMs - (now - last);
                            long retrySec = (retryMs + 999) / 1000;
                            throw new RateLimitExceededException(retrySec);
                        }
                        return now;
                    });
                }
            }

            chain.doFilter(toNext, response);

        } catch (RateLimitExceededException ex) {
            // Resolve with the same (possibly wrapped) request so attributes are visible to handlers/aspects
            exceptionResolver.resolveException(toNext, response, null, ex);
        }
    }

    /**
     * Extracts {@code userId} from a JSON request body.
     *
     * @param json raw request body
     * @return userId string or {@code null} if absent/invalid
     */
    private String extractUserIdFromJson(String json) {
        try {
            if (json == null || json.isBlank()) return null;
            JsonNode node = objectMapper.readTree(json);
            JsonNode userIdNode = node.get("userId");
            return (userIdNode != null) ? userIdNode.asText() : null;
        } catch (Exception e) {
            return null;
        }
    }
}