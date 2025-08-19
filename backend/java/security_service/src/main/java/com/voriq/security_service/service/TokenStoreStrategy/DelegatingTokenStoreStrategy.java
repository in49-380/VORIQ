package com.voriq.security_service.service.TokenStoreStrategy;

import com.voriq.security_service.exception_handler.exception.RestException;
import com.voriq.security_service.exception_handler.exception.StrategyNotFoundException;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Delegating orchestrator over multiple {@link TokenStoreStrategy} backends.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Selects the first <em>applicable</em> strategy according to Spring's {@link Order}
 *       (lower order value = higher priority). The delegator itself is excluded from candidates.</li>
 *   <li>On activation of a different strategy class, if it is {@link RedisTokenStoreStrategy},
 *       triggers a one-time migration from in-memory to Redis via {@link TokenMigrationService}.</li>
 *   <li>Propagates business errors as {@link RestException} and treats other {@link RuntimeException}s
 *       as infrastructure failures with fallback to the next applicable strategy.</li>
 * </ul>
 *
 * <p><b>Thread-safety:</b> The class is stateless except for {@code lastActiveClass}, used only to
 * detect transitions between active strategies. Migration is invoked best-effort and should be
 * idempotent on the service side.</p>
 *
 * <p><b>Proxies:</b> Class comparisons are made against target classes via {@link AopUtils#getTargetClass(Object)}
 * to be proxy-agnostic (JDK/CGLIB).</p>
 *
 * @author RsLan
 * @since 1.0.0
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Primary
public class DelegatingTokenStoreStrategy implements TokenStoreStrategy {

    private final List<TokenStoreStrategy> strategies;
    private final TokenMigrationService migrationService;

    public static final String DEFAULT_SET_VALUE = "unknown";

    /**
     * Remembers the class of the last strategy that was observed as active,
     * to avoid re-triggering migration on every call.
     */
    private volatile Class<?> lastActiveClass = null;

    /**
     * Creates a delegator that preserves Spring's ordering and excludes itself from the candidates list.
     *
     * @param strategies       all {@link TokenStoreStrategy} beans from the Spring context (may include this delegator)
     * @param migrationService service used to migrate accumulated in-memory data back to Redis
     */
    public DelegatingTokenStoreStrategy(List<TokenStoreStrategy> strategies,
                                        TokenMigrationService migrationService) {
        this.migrationService = migrationService;
        this.strategies = strategies.stream()
                .filter(s -> !DelegatingTokenStoreStrategy.class.isAssignableFrom(AopUtils.getTargetClass(s)))
                .toList();
    }

    /**
     * The delegator is always applicable; actual selection happens per concrete strategy.
     *
     * @return always {@code true}
     */
    @Override
    public boolean isApplicable() {
        return true;
    }

    /**
     * Saves a token using the first applicable strategy. Falls back on infrastructure failure.
     *
     * @param token  a non-null token string
     * @param userId the owner's UUID
     * @throws RestException             if business rules are violated by the chosen strategy
     * @throws StrategyNotFoundException if no applicable strategy is available
     * @throws RuntimeException          if all applicable strategies fail with infrastructure errors
     */
    @Override
    public void saveToken(String token, UUID userId) {
        executeWithFallback(s -> {
            s.saveToken(token, userId);
            return null;
        });
    }

    /**
     * Checks token validity using the first applicable strategy. Falls back on infrastructure failure.
     *
     * @param token token to check
     * @return {@code true} if the token is known and valid; {@code false} otherwise
     * @throws RestException             if business rules are violated by the chosen strategy
     * @throws StrategyNotFoundException if no applicable strategy is available
     * @throws RuntimeException          if all applicable strategies fail with infrastructure errors
     */
    @Override
    public boolean isValid(String token) {
        return executeWithFallback(s -> s.isValid(token));
    }

    /**
     * Retrieves an arbitrary member from a set-like collection stored under the given key
     * (e.g., a Redis Set) using the first applicable strategy.
     *
     * <p>Non-destructive: the returned value is not removed from the set. If the key does not exist
     * or the set is empty, returns {@code null}.</p>
     *
     * @param key non-null logical key identifying the set/collection
     * @return a set member if present; {@code null} if absent or empty
     * @throws StrategyNotFoundException if no applicable strategy is available
     * @throws RuntimeException          if all applicable strategies fail with infrastructure errors
     */
    @Override
    public String getSetValueByKey(String key) {
        return executeWithFallback(s -> s.getSetValueByKey(key));
    }

    /**
     * Revokes (invalidates) the given token using the first applicable strategy.
     *
     * <p>Delegates to underlying {@code TokenStoreStrategy} implementations via
     * {@code executeWithFallback(...)} in precedence order. If the current strategy
     * fails due to infrastructure issues (e.g., network/Redis/DB outage), the next
     * applicable strategy is attempted. Functional "not found" semantics are left to
     * the strategy implementation but the operation should be idempotent.</p>
     *
     * @param token non-null token identifier to revoke/blacklist
     * @return {@code true} if the chosen strategy reports the token was revoked/blacklisted;
     * {@code false} if nothing changed (e.g., token already absent or unknown)
     * @throws StrategyNotFoundException if no applicable strategy is available
     * @throws RuntimeException          if all applicable strategies fail with infrastructure errors
     */
    @Override
    public boolean revokeToken(String token) {
        return executeWithFallback(s -> s.revokeToken(token));
    }

    /**
     * Functional operation executed against a chosen {@link TokenStoreStrategy}.
     *
     * @param <T> result type
     */
    @FunctionalInterface
    private interface StrategyOp<T> {
        T apply(TokenStoreStrategy s);
    }

    /**
     * Executes an operation against the first applicable strategy with robust fallback semantics.
     *
     * <p>Algorithm:</p>
     * <ol>
     *   <li>Skip strategies where {@code isApplicable() == false}.</li>
     *   <li>On activation of a new target class, conditionally trigger migration to Redis.</li>
     *   <li>Invoke the operation:
     *     <ul>
     *       <li>{@link RestException} is rethrown immediately (business error).</li>
     *       <li>Other {@link RuntimeException}s are treated as infrastructure failures; try next strategy.</li>
     *     </ul>
     *   </li>
     *   <li>If all applicable strategies failed with infra errors, rethrow the last one.</li>
     *   <li>If there were no applicable strategies at all, throw {@link StrategyNotFoundException}.</li>
     * </ol>
     *
     * @param op  operation to perform against the chosen strategy
     * @param <T> result type
     * @return operation result
     * @throws RestException             bubbled up as-is
     * @throws StrategyNotFoundException if no applicable strategy is available
     * @throws RuntimeException          last infrastructure error if all applicable strategies failed
     */
    private <T> T executeWithFallback(StrategyOp<T> op) {
        RuntimeException lastInfraError = null;

        for (TokenStoreStrategy s : strategies) {
            if (!s.isApplicable()) continue;

            maybeMigrateOnActivation(s);

            try {
                return op.apply(s);
            } catch (RuntimeException ex) {
                if (ex instanceof RestException) {
                    throw ex;
                }
                lastInfraError = ex;
            }
        }

        if (lastInfraError != null) throw lastInfraError;
        throw new StrategyNotFoundException("No available TokenStoreStrategy");
    }

    /**
     * If the active strategy's target class differs from the previously observed one and is Redis-based,
     * attempts to migrate accumulated in-memory data to Redis (only if not empty).
     * Any {@link RuntimeException} during migration is swallowed to avoid breaking the main flow.
     *
     * @param active the strategy chosen for the current operation
     */
    private void maybeMigrateOnActivation(TokenStoreStrategy active) {
        // compare by target classes to be proxy-agnostic
        Class<?> now = AopUtils.getTargetClass(active);
        Class<?> prev = lastActiveClass;

        if (prev == null || !prev.equals(now)) {
            if (active instanceof RedisTokenStoreStrategy) {
                try {
                    if (!migrationService.isInMemoryEmpty()) {
                        migrationService.migrateToRedis();
                    }
                } catch (RuntimeException ignored) {
                }
            }
            lastActiveClass = now;
        }
    }
}
