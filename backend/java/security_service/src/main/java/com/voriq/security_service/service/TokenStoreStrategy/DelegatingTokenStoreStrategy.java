package com.voriq.security_service.service.TokenStoreStrategy;

import com.voriq.security_service.exception_handler.exception.RestException;
import com.voriq.security_service.exception_handler.exception.StrategyNotFoundException;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Delegating orchestrator over multiple {@link TokenStoreStrategy} backends.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Selects the first <em>applicable</em> strategy according to Spring's {@link Order}
 *       (lowest order value = higher priority). The delegator itself is excluded from the list.</li>
 *   <li>On activation of a different strategy class, if it is {@link RedisTokenStoreStrategy},
 *       triggers a one-time migration from in-memory to Redis via {@link TokenMigrationService}.</li>
 *   <li>Propagates business errors as {@link RestException} and treats other {@link RuntimeException}
 *       as infrastructure failures, falling back to the next applicable strategy.</li>
 * </ul>
 * <p>
 * Thread-safety: the class is stateless except for {@code lastActiveClass}, which is used only to
 * detect transitions between active strategies. Migration is invoked best-effort and should be
 * idempotent on the service side.
 * </p>
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

    /**
     * Remembers the class of the last strategy that was observed as active,
     * to avoid re-triggering migration on every call.
     */
    private volatile Class<?> lastActiveClass = null;

    /**
     * Creates a delegator that sorts and filters the provided strategies according to {@link Order}.
     *
     * @param strategies       all strategies from the Spring context (including this delegator)
     * @param migrationService service used to migrate accumulated in-memory data back to Redis
     */
    public DelegatingTokenStoreStrategy(List<TokenStoreStrategy> strategies,
                                        TokenMigrationService migrationService) {
        this.migrationService = migrationService;
        this.strategies = strategies.stream()
                .filter(s -> s != this)
                .sorted(Comparator.comparingInt(this::orderOf))
                .toList();
    }

    private int orderOf(TokenStoreStrategy s) {
        Order order = s.getClass().getAnnotation(Order.class);
        return order != null ? order.value() : Ordered.LOWEST_PRECEDENCE;
    }

    /**
     * The delegator is always applicable; actual selection happens per concrete strategy.
     */
    @Override
    public boolean isApplicable() {
        return true;
    }

    /**
     * If the active strategy class differs from the previously observed one and is Redis-based,
     * attempts to migrate accumulated in-memory data to Redis (only if not empty).
     * Any {@link RuntimeException} during migration is swallowed to avoid breaking the main flow.
     *
     * @param active the strategy chosen for the current operation
     */
    private void maybeMigrateOnActivation(TokenStoreStrategy active) {
        Class<?> now = active.getClass();
        Class<?> prev = lastActiveClass;
        if (prev == null || !prev.equals(now)) {
            if (active instanceof RedisTokenStoreStrategy) {
                try {
                    if (!migrationService.isInMemoryEmpty()) {
                        migrationService.migrateToRedis();
                    }
                } catch (RuntimeException ex) {
                    // best-effort; do not fail the request due to migration issues
                }
            }
            lastActiveClass = now;
        }
    }

    /**
     * Saves a token using the first applicable strategy. Falls back on infra failure.
     *
     * @throws RestException             if business rules are violated by the chosen strategy
     * @throws StrategyNotFoundException if no applicable strategy is available
     * @throws RuntimeException          if all applicable strategies fail with infrastructure errors
     */
    @Override
    public void saveToken(String token, UUID userId) {
        RuntimeException lastInfraError = null;

        for (TokenStoreStrategy s : strategies) {
            if (!s.isApplicable()) continue;

            maybeMigrateOnActivation(s);

            try {
                s.saveToken(token, userId);
                return;
            } catch (RestException re) {
                throw re;
            } catch (RuntimeException infra) {
                lastInfraError = infra;
            }
        }

        if (lastInfraError != null) throw lastInfraError;
        throw new StrategyNotFoundException("No available TokenStoreStrategy");
    }

    /**
     * Checks token validity using the first applicable strategy. Falls back on infra failure.
     *
     * @throws RestException             if business rules are violated by the chosen strategy
     * @throws StrategyNotFoundException if no applicable strategy is available
     * @throws RuntimeException          if all applicable strategies fail with infrastructure errors
     */
    @Override
    public boolean isValid(String token) {
        RuntimeException lastInfraError = null;

        for (TokenStoreStrategy s : strategies) {
            if (!s.isApplicable()) continue;

            maybeMigrateOnActivation(s);

            try {
                return s.isValid(token);
            } catch (RestException re) {
                throw re;
            } catch (RuntimeException infra) {
                lastInfraError = infra;
            }
        }

        if (lastInfraError != null) throw lastInfraError;
        throw new StrategyNotFoundException("No available TokenStoreStrategy");
    }
}
