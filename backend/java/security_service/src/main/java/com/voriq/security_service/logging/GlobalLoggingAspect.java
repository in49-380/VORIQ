package com.voriq.security_service.logging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voriq.security_service.domain.dto.TokenRequestDto;
import com.voriq.security_service.filter.RepeatableBodyRequestWrapper;
import com.voriq.security_service.filter.TokenRateLimitFilter;
import com.voriq.security_service.service.TokenStoreStrategy.TokenStoreStrategy;
import com.voriq.security_service.utilitie.TokenUtilities;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.voriq.security_service.service.TokenStoreStrategy.DelegatingTokenStoreStrategy.DEFAULT_SET_VALUE;
import static com.voriq.security_service.utilitie.TokenUtilities.getMaskedUuid;

/**
 * Global AOP logging for token issuance/validation flow and centralized exception handling.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Log successful token issuance from {@code TokenController.issue(..)} at INFO level.</li>
 *   <li>Log successful token validation from {@code TokenController.validate(..)} at INFO level.</li>
 *   <li>Log handled errors around Spring's {@code HandlerExceptionResolver} at ERROR level
 *       with resolved HTTP status and best-effort user identification.</li>
 *   <li>Log health and usage of token stores (Redis / in-memory) and migration results.</li>
 *   <li>Log user blocking results.</li>
 * </ul>
 *
 * <h3>User identification</h3>
 * <p>Resolution order:</p>
 * <ol>
 *   <li>Request attribute {@link TokenRateLimitFilter#ATTR_USER_ID} (set by rate-limit filter).</li>
 *   <li>Request parameter {@code userId}.</li>
 *   <li>Header {@code X-User-Id}.</li>
 *   <li>Cached body via {@link RepeatableBodyRequestWrapper} → JSON field {@code userId}.</li>
 *   <li>Falls back to {@code DEFAULT_SET_VALUE} if nothing is available.</li>
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
    private static final Logger STORE_LOG = LoggerFactory.getLogger("com.voriq.redis.health");

    private final TokenStoreStrategy tokenStoreStrategy;
    /**
     * Request-scoped flag to ensure an exception is logged only once.
     */
    private static final String EX_LOGGED_ATTR = "EX_LOGGED";

    @Value("${token.access-token-expiration-ms}")
    private long accessExpirationMs;

    // ===================== Issue token =====================

    /**
     * Pointcut for the token issuance endpoint in the controller.
     * Matches {@code TokenController.issue(..)} regardless of argument types.
     */
    @Pointcut("execution(* com.voriq.security_service.controller.TokenController.issue(..))")
    public void issueMethodTokenController() {
    }

    /**
     * Logs a successful token issuance at INFO level with user id and a Code=200 marker.
     *
     * <p>Triggered after a normal return from {@code TokenController.issue(..)}.
     * Extracts {@link TokenRequestDto} from method arguments to log the {@code userId}.</p>
     *
     * @param joinPoint join point containing method arguments (used to extract {@link TokenRequestDto})
     */
    @AfterReturning("issueMethodTokenController()")
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

    // ===================== Validate token =====================

    /**
     * Pointcut for the token validate endpoint in the controller.
     * Matches {@code TokenController.validate(..)} regardless of argument types.
     */
    @Pointcut("execution(* com.voriq.security_service.controller.TokenController.validate(..))")
    public void validateMethodTokenController() {
    }

    /**
     * After-returning advice that logs a successful token validation.
     *
     * <p>Runs only when the controller's {@code validate(...)} method returns normally.
     * Extracts the first {@code String} argument (assumed to be the bearer token), masks it via
     * {@link TokenUtilities#getMaskedUuid(String)},
     * and logs at INFO. The HTTP status is read from the returned {@link ResponseEntity}; if
     * {@code result} is {@code null}, the status defaults to {@code 204}.</p>
     *
     * <p><b>Notes:</b></p>
     * <ul>
     *   <li>Raw tokens are never logged; only masked values are emitted.</li>
     *   <li>If the controller accepts a {@code UUID} instead of a {@code String}, adjust the advice accordingly.</li>
     *   <li>To log failures/exceptions, add a corresponding {@code @AfterThrowing} advice.</li>
     * </ul>
     *
     * @param joinPoint join point used to access method arguments (expects a {@code String} token)
     * @param result    controller response used to derive the HTTP status (typically {@code 204 No Content})
     */
    @AfterReturning(pointcut = "validateMethodTokenController()", returning = "result")
    public void afterReturningForValidateMethod(JoinPoint joinPoint, ResponseEntity<?> result) {
        String now = LocalDateTime.now().format(FMT);

        String token = null;
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof String u) {
                token = u;
                break;
            }
        }

        String userId = tokenStoreStrategy.getSetValueByKey(token);
        String masked = (token != null) ? getMaskedUuid(token) : "<absent>";
        int code = (result != null) ? result.getStatusCode().value() : 204;

        log.info("[INFO] {} - User with ID {} validated token {} succeeded. Code= {}", now, userId, masked, code);
    }

    /**
     * Pointcut that matches the {@code revoke(...)} method in {@code TokenController}.
     *
     * <p>Matches any {@code revoke} method signature in
     * {@code com.voriq.security_service.controller.TokenController}, regardless of arguments.
     * Keep this expression in sync if the controller class or method name changes.</p>
     */
    @Pointcut("execution(* com.voriq.security_service.controller.TokenController.revoke(..))")
    public void revokeMethodTokenController() {
    }

    /**
     * After-returning advice that logs a successful token revocation.
     *
     * <p>Runs only when the controller's {@code revoke(...)} method returns normally.
     * Extracts the first {@code String} argument (assumed to be the bearer token), masks it via
     * {@link TokenUtilities#getMaskedUuid(String)},
     * and logs at INFO. The HTTP status is read from the returned {@link org.springframework.http.ResponseEntity};
     * if {@code result} is {@code null}, the status defaults to {@code 204}.</p>
     *
     * <p><b>Notes:</b></p>
     * <ul>
     *   <li>Raw tokens are never logged; only masked values are emitted.</li>
     *   <li>If the controller accepts a {@code UUID} instead of a {@code String}, adjust the advice accordingly.</li>
     *   <li>To log failures/exceptions, add a corresponding {@code @AfterThrowing} advice.</li>
     *   <li>If no {@code String} argument is present, the token is rendered as {@code "<absent>"}.</li>
     * </ul>
     *
     * @param joinPoint join point used to access method arguments (expects a {@code String} token)
     * @param result    controller response used to derive the HTTP status (typically {@code 204 No Content})
     */
    @AfterReturning(pointcut = "revokeMethodTokenController()", returning = "result")
    public void afterReturningForRevokeMethod(JoinPoint joinPoint, ResponseEntity<?> result) {
        String now = LocalDateTime.now().format(FMT);

        String token = null;
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof String u) {
                token = u;
                break;
            }
        }
        String masked = (token != null) ? getMaskedUuid(token) : "<absent>";
        int code = (result != null) ? result.getStatusCode().value() : 204;
        log.info("[INFO] {} - Token {} revoked succeeded. Code= {}", now, masked, code);
    }

    // ===================== Centralized exception logging =====================

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
                ex != null ? ex.getMessage() : "Unknown error");
    }

    /**
     * Resolves {@code userId} from multiple request locations (attribute, param, header, cached body).
     *
     * @param request current request (may be a {@link RepeatableBodyRequestWrapper})
     * @return a non-empty string user id if found; otherwise {@code DEFAULT_SET_VALUE}
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
        return DEFAULT_SET_VALUE;
    }

    // ===================== Token store (Redis / in-memory) =====================

    /**
     * Pointcut for Redis-backed {@code TokenStoreStrategy#isApplicable(..)}.
     * <p>Targets the interface method (to work with JDK proxies) and narrows to the Redis bean by name.</p>
     * <p><b>Note:</b> Adjust {@code bean(redisTokenStoreStrategy)} if the bean name differs.</p>
     */
    @Pointcut("execution(boolean com.voriq.security_service.service.TokenStoreStrategy.TokenStoreStrategy.isApplicable(..)) " +
            "&& bean(redisTokenStoreStrategy)")
    public void isApplicableMethodRedisTokenStoreStrategy() {
    }

    /**
     * Logs the outcome of Redis connectivity/probing.
     *
     * <p>On {@code true}: emits an {@code INFO} line indicating Redis is UP and will be used as storage.
     * On {@code false}: emits a {@code WARN} line indicating Redis is not available.</p>
     *
     * @param result result of {@code isApplicable()} — {@code true} if Redis answered PONG (and optional write check passed)
     */
    @AfterReturning(value = "isApplicableMethodRedisTokenStoreStrategy()", returning = "result")
    public void afterReturningIsApplicableMethodRedisTokenStoreStrategy(boolean result) {
        String now = LocalDateTime.now().format(FMT);

        if (result) {
            STORE_LOG.info("[INFO] {} - Redis health check: status=UP ping=PONG writeCheck=OK. Redis is used as storage.", now);
        } else {
            STORE_LOG.warn("[WARN] {} - Redis health check: status=DOWN reason=not-pong. Redis is not available.", now);
        }
    }

    /**
     * Pointcut for in-memory {@code TokenStoreStrategy#isApplicable(..)}.
     * <p>Narrows by bean name to the in-memory implementation.</p>
     * <p><b>Note:</b> Adjust {@code bean(inMemoryTokenStoreStrategy)} if the bean name differs.</p>
     */
    @Pointcut("execution(boolean com.voriq.security_service.service.TokenStoreStrategy.TokenStoreStrategy.isApplicable(..)) " +
            "&& bean(inMemoryTokenStoreStrategy)")
    public void isApplicableMethodInMemoryTokenStoreStrategy() {
    }

    /**
     * Logs which storage is in effect when the in-memory strategy reports applicable.
     *
     * <p>On {@code true}: in-memory storage is active; on {@code false}: in-memory storage is not used.</p>
     *
     * @param result result of {@code isApplicable()} for the in-memory strategy
     */
    @AfterReturning(value = "isApplicableMethodInMemoryTokenStoreStrategy()", returning = "result")
    public void afterReturningIsApplicableMethodInMemoryTokenStoreStrategy(boolean result) {
        String now = LocalDateTime.now().format(FMT);

        if (result) {
            STORE_LOG.info("[INFO] {} - In-memory storage is used as token store.", now);
        } else {
            STORE_LOG.warn("[WARN] {} - In-memory storage is not used.", now);
        }
    }

    // ===================== Migration =====================

    /**
     * Pointcut for migration operation from in-memory to Redis storage.
     * Matches {@code TokenMigrationService.migrateToRedis(..)}.
     */
    @Pointcut("execution(* com.voriq.security_service.service.TokenStoreStrategy.TokenMigrationService.migrateToRedis(..)) ")
    public void migrateToRedisMethodTokenMigrationService() {
    }

    /**
     * Logs the result of data migration from in-memory storage to Redis.
     *
     * <p>On {@code true}: migration succeeded; on {@code false}: migration failed.</p>
     *
     * @param result result returned by {@code migrateToRedis(..)}
     */
    @AfterReturning(value = "migrateToRedisMethodTokenMigrationService()", returning = "result")
    public void afterReturningMigrateToRedisMethodTokenMigrationService(boolean result) {
        String now = LocalDateTime.now().format(FMT);

        if (result) {
            STORE_LOG.info("[INFO] {} - The data in memory was successfully transferred to Redis.", now);
        } else {
            STORE_LOG.error("[ERROR] {} - Data from memory has not been migrated to Redis.", now);
        }
    }

    // ===================== Blocking =====================

    /**
     * Pointcut for user blocking operation.
     * <p>Binds the first {@code UUID} argument (userId) of {@code BlockService.block(..)} for use in advices.</p>
     */
    @Pointcut("execution(boolean com.voriq.security_service.service.interfaces.BlockService.block(..)) && args(userId,..)")
    public void blockCall(UUID userId) {
    }

    /**
     * Logs the outcome of user blocking.
     *
     * <p>On {@code true}: user is blocked for {@code accessExpirationMs} seconds (WARN).
     * On {@code false}: blocking failed (ERROR).</p>
     *
     * @param userId user identifier passed to {@code BlockService.block(..)}
     * @param result result returned by {@code BlockService.block(..)}
     */
    @AfterReturning(pointcut = "blockCall(userId)", returning = "result", argNames = "userId,result")
    public void afterBlock(UUID userId, boolean result) {
        String now = LocalDateTime.now().format(FMT);
        if (result) {
            log.warn("[WARN] {} - User {} was blocked for {} seconds", now, userId, accessExpirationMs / 1000);
        } else {
            log.error("[ERROR] {} - Error blocking user {}.", now, userId);
        }
    }

    @Pointcut("execution(boolean com.voriq.security_service.service.interfaces.BlockService.removeBlock(..)) && args(userId,..)")
    public void removeBlockCall(UUID userId) {
    }

    @AfterReturning(pointcut = "removeBlockCall(userId)", returning = "result", argNames = "userId,result")
    public void afterRemoveBlock(UUID userId, boolean result) {
        String now = LocalDateTime.now().format(FMT);
        if (result) {
            log.warn("[INFO] {} - User {} was unblocked.", now, userId);
        } else {
            log.error("[ERROR] {} - Error unblocking user {}.", now, userId);
        }
    }
}
