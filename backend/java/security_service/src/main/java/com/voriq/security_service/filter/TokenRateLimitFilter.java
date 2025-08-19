package com.voriq.security_service.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voriq.security_service.exception_handler.exception.RateLimitExceededException;
import com.voriq.security_service.service.TokenStoreStrategy.TokenStoreStrategy;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import static com.voriq.security_service.config.SecurityConfig.ISSUE_URL;
import static com.voriq.security_service.config.SecurityConfig.VALIDATE_URL;
import static com.voriq.security_service.utilitie.TokenUtilities.extractTokenFromRequest;
import static com.voriq.security_service.utilitie.TokenUtilities.isUuid;

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
 *   <li>Uses an atomic {@link ConcurrentHashMap#compute(Object, BiFunction)} to prevent races.</li>
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
     * Minimal interval between requests from the same logical user for the
     * <b>issue</b> endpoint (milliseconds).
     *
     * <p>Loaded from property {@code rate.limit-ms.issue}.</p>
     */
    @Value("${rate.limit-ms.issue}")
    private long issueRequestLimitIntervalMs;

    /**
     * Minimal interval between requests from the same logical user for the
     * <b>validate</b> endpoint (milliseconds).
     *
     * <p>Loaded from property {@code rate.limit-ms.validate}.</p>
     */
    @Value("${rate.limit-ms.validate}")
    private long validateRequestLimitIntervalMs;

    /**
     * Request attribute name used to propagate the resolved {@code userId}
     * to downstream components (e.g., AOP logging).
     *
     * <p>Set by this filter when a {@code userId} is successfully extracted from the request body.
     * Consumers should treat it as best-effort and fall back if absent.</p>
     */
    public static final String ATTR_USER_ID = "X_USER_ID";

    /**
     * Last-seen timestamps (epoch millis) for rate-limiting.
     *
     * <p>Thread-safe map used with {@link ConcurrentHashMap#compute(Object, BiFunction)}.</p>
     */
    private final Map<String, Long> requestTimestamps = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HandlerExceptionResolver exceptionResolver;
    private final TokenStoreStrategy tokenStoreStrategy;

    public TokenRateLimitFilter(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver, TokenStoreStrategy tokenStoreStrategy) {
        this.exceptionResolver = exceptionResolver;
        this.tokenStoreStrategy = tokenStoreStrategy;
    }

    /**
     * Applies per-user rate limiting for the token endpoints.
     *
     * <p>Behavior:</p>
     * <ul>
     *   <li>Normalizes the request path (removes the {@code contextPath}) and handles only:
     *     <ul>
     *       <li><b>POST</b> {@link com.voriq.security_service.config.SecurityConfig#ISSUE_URL}</li>
     *       <li><b>GET</b>  {@link com.voriq.security_service.config.SecurityConfig#VALIDATE_URL}</li>
     *     </ul>
     *     All other requests pass through unchanged.
     *   </li>
     *   <li>For <b>POST {ISSUE_URL}</b>:
     *     <ul>
     *       <li>Reads the body once and wraps the request with
     *           {@link com.voriq.security_service.filter.RepeatableBodyRequestWrapper} so downstream can read it again,</li>
     *       <li>Extracts {@code userId} from the JSON body,</li>
     *       <li>Uses the configured interval {@code rate.limit-ms.issue} (milliseconds).</li>
     *     </ul>
     *   </li>
     *   <li>For <b>GET {VALIDATE_URL}</b>:
     *     <ul>
     *       <li>Extracts the Bearer token from the {@code Authorization} header (no body read),</li>
     *       <li>Resolves {@code userId} via
     *           {@link com.voriq.security_service.service.TokenStoreStrategy.TokenStoreStrategy#getSetValueByKey(String)},</li>
     *       <li>Uses the configured interval {@code rate.limit-ms.validate} (milliseconds).</li>
     *     </ul>
     *   </li>
     *   <li>If a {@code userId} is resolved, stores it under {@link #ATTR_USER_ID} and enforces a per-user
     *       sliding window using an internal timestamp map with
     *       {@link java.util.concurrent.ConcurrentHashMap#compute(Object, java.util.function.BiFunction)}.
     *       On violation, throws {@code RateLimitExceededException}, which is caught and delegated to
     *       {@link org.springframework.web.servlet.HandlerExceptionResolver}.</li>
     * </ul>
     *
     * <p><b>Notes:</b></p>
     * <ul>
     *   <li>If {@code userId} cannot be resolved, the request is not rate-limited.</li>
     *   <li>The request body is consumed and re-exposed <i>only</i> for <b>POST {ISSUE_URL}</b>.</li>
     *   <li>Intervals are read from configuration: {@code rate.limit-ms.issue} and {@code rate.limit-ms.validate}.</li>
     * </ul>
     *
     * @param request  current HTTP request; for <b>POST {ISSUE_URL}</b> the body is consumed and re-wrapped
     * @param response current HTTP response
     * @param chain    next filter in the chain
     * @throws ServletException if thrown by the downstream filter chain
     * @throws IOException      if reading the request body fails (for POST)
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        HttpServletRequest toNext = request;

        try {
            final String method = request.getMethod();
            final String path = normalizePath(request);

            final boolean isIssue = HttpMethod.POST.name().equalsIgnoreCase(method) && ISSUE_URL.equals(path);
            final boolean isValidate = HttpMethod.GET.name().equalsIgnoreCase(method) && VALIDATE_URL.equals(path);

            if (!isIssue && !isValidate) {
                chain.doFilter(request, response);
                return;
            }

            final long limitIntervalMs = isValidate ? validateRequestLimitIntervalMs : issueRequestLimitIntervalMs;

            String userId = null;

            if (isIssue) {
                byte[] bodyBytes = request.getInputStream().readAllBytes();
                toNext = new RepeatableBodyRequestWrapper(request, bodyBytes);
                String body = new String(bodyBytes, StandardCharsets.UTF_8);
                userId = extractUserIdFromJson(body);
            } else {
                String token = extractTokenFromRequest(request);
                if (token != null && !token.isBlank()) {
                    String mapped = tokenStoreStrategy.getSetValueByKey(token);
                    if (isUuid(mapped)) {
                        userId = mapped;
                    }
                }
            }

            if (userId != null && !userId.isBlank()) {
                toNext.setAttribute(ATTR_USER_ID, userId);

                requestTimestamps.compute(userId, (k, last) -> {
                    long now = System.currentTimeMillis();
                    if (last != null && now - last < limitIntervalMs) {
                        long retryMs = limitIntervalMs - (now - last);
                        long retrySec = (retryMs + 999) / 1000;
                        throw new RateLimitExceededException(retrySec);
                    }
                    return now;
                });
            }

            chain.doFilter(toNext, response);

        } catch (RateLimitExceededException ex) {
            exceptionResolver.resolveException(toNext, response, null, ex);
        }
    }

    /**
     * Returns the request URI without the servlet context path.
     *
     * <p>Example: for {@code contextPath="/api"} and {@code requestURI="/api/v1/tokens/issue"}
     * returns {@code "/v1/tokens/issue"}.</p>
     *
     * @param req current request
     * @return URI relative to {@code contextPath}, starting with {@code /}
     */
    private static String normalizePath(HttpServletRequest req) {
        String uri = req.getRequestURI();
        String ctx = req.getContextPath();
        return (ctx != null && !ctx.isEmpty() && uri.startsWith(ctx))
                ? uri.substring(ctx.length())
                : uri;
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