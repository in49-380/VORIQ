package com.voriq.security_service.logging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voriq.security_service.domain.dto.TokenRequestDto;
import com.voriq.security_service.filter.RepeatableBodyRequestWrapper;
import com.voriq.security_service.filter.TokenRateLimitFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Global AOP logging for token issuance flow and exception handling.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Log successful token issuance from {@code TokenController.issue(..)} at INFO level.</li>
 *   <li>Log handled errors around Spring's {@code HandlerExceptionResolver} at ERROR level
 *       with resolved HTTP status and best-effort user identification.</li>
 * </ul>
 *
 * <h3>User identification</h3>
 * <p>Resolution order:</p>
 * <ol>
 *   <li>Request attribute {@link TokenRateLimitFilter#ATTR_USER_ID} (set by rate-limit filter).</li>
 *   <li>Request parameter {@code userId}.</li>
 *   <li>Header {@code X-User-Id}.</li>
 *   <li>Cached body via {@link RepeatableBodyRequestWrapper} → JSON field {@code userId}.</li>
 *   <li>Falls back to {@code "unknown"} if nothing is available.</li>
 * </ol>
 *
 * <h3>Notes</h3>
 * <ul>
 *   <li>Logs are formatted with a fixed timestamp pattern {@code dd.MM.yyyy HH:mm}.</li>
 *   <li>A single-exception-per-request guarantee is ensured via the {@code EX_LOGGED} attribute.</li>
 * </ul>
 *
 * @author RsLan
 * @since 1.0.0
 */
@Slf4j(topic = "com.voriq.security_service.aop")
@Aspect
@Component
@RequiredArgsConstructor
public class GlobalLoggingAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    /** Request-scoped flag to ensure an exception is logged only once. */
    private static final String EX_LOGGED_ATTR = "EX_LOGGED";

    @Value("${token.access-token-expiration-ms}")
    private long accessExpirationMs;

    /**
     * Pointcut for the token issuance endpoint in the controller.
     */
    @Pointcut("execution(* com.voriq.security_service.controller.TokenController.issue(..))")
    public void issueMethodTokenController() {
    }

    /**
     * Logs a successful token issuance at INFO level with user id and a Code=200 marker.
     *
     * @param joinPoint join point containing method arguments (used to extract {@link TokenRequestDto})
     */
    @AfterReturning(pointcut = "issueMethodTokenController()", returning = "result")
    public void afterReturningForIssueMethod(JoinPoint joinPoint) {
        String now = LocalDateTime.now().format(FMT);
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof TokenRequestDto dto) {
                UUID userId = dto.getUserId();
                log.info("[INFO] {} - User with ID {} requested token. Code= 200. Result: Success.", now, userId);
                break;
            }
        }
    }

    /**
     * Around advice for Spring's exception resolver. Proceeds with resolution, then logs
     * the error once per request using the resolved HTTP status if available.
     *
     * @param pjp proceeding join point for {@code HandlerExceptionResolver.resolveException(..)}
     * @return the original {@link ModelAndView} (or {@code null}) returned by the resolver
     * @throws Throwable if the underlying invocation throws
     */
    @Around("execution(* org.springframework.web.servlet.HandlerExceptionResolver.resolveException(..))")
    public Object exceptionAround(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        HttpServletRequest request = (HttpServletRequest) args[0];
        HttpServletResponse response = (HttpServletResponse) args[1];
        Throwable ex = (Throwable) args[3];

        Object mvObj = pjp.proceed();

        boolean handled = (mvObj instanceof ModelAndView)
                || response.getStatus() >= 400
                || response.isCommitted();

        if (handled && request.getAttribute(EX_LOGGED_ATTR) == null) {
            request.setAttribute(EX_LOGGED_ATTR, Boolean.TRUE);
            logOnce(request, response, ex);
        }
        return mvObj;
    }

    /**
     * Emits a single ERROR log line for the given request/response/exception triple.
     * Uses best-effort user id resolution and resolved HTTP status (defaults to 500).
     *
     * @param request  current request
     * @param response current response
     * @param ex       handled exception
     */
    private void logOnce(HttpServletRequest request, HttpServletResponse response, Throwable ex) {
        String now = LocalDateTime.now().format(FMT);
        String userId = resolveUserId(request);
        int status = response.getStatus() > 0 ? response.getStatus() : 500;

        log.error("[ERROR] {} - User with ID {}. Code= {} : {}",
                now,
                userId,
                status,
                ex.getMessage());
    }

    /**
     * Resolves {@code userId} from multiple request locations (attribute, param, header, cached body).
     *
     * @param request current request (may be a {@link RepeatableBodyRequestWrapper})
     * @return a non-empty string user id if found; otherwise {@code "unknown"}
     */
    private String resolveUserId(HttpServletRequest request) {

        Object attr = request.getAttribute(TokenRateLimitFilter.ATTR_USER_ID);
        if (attr instanceof String s && !s.isBlank()) return s;

        String userId = request.getParameter("userId");
        if (userId != null && !userId.isBlank()) return userId;

        userId = request.getHeader("X-User-Id");
        if (userId != null && !userId.isBlank()) return userId;

        if (request instanceof RepeatableBodyRequestWrapper r) {
            String body = r.getCachedBody();
            if (body != null && !body.isBlank()) {
                try {
                    JsonNode node = objectMapper.readTree(body);
                    JsonNode userIdNode = node.get("userId");
                    if (userIdNode != null && !userIdNode.asText().isBlank()) {
                        return userIdNode.asText();
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return "unknown";
    }
}
