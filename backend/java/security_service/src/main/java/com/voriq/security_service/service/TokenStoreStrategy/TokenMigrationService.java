package com.voriq.security_service.service.TokenStoreStrategy;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Migrates in-memory token/blacklist state to Redis when the primary backend becomes available again.
 *
 * <h3>What gets migrated</h3>
 * <ul>
 *   <li><b>User block flags</b> from {@code InMemoryTokenStoreStrategy.blacklistUntil} → Redis key
 *       {@code <blockedPrefix><userId>} with TTL equal to the remaining block duration.</li>
 *   <li><b>Active tokens</b> from {@code InMemoryTokenStoreStrategy.byUser} → Redis:
 *     <ul>
 *       <li>Token key: {@code token} (a Set containing {@code userId}) with TTL equal to the remaining token lifetime.</li>
 *       <li>User index key: {@code userId.toString()} (a Set of tokens) without TTL.</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h3>Idempotency & failure model</h3>
 * <ul>
 *   <li>Operations are designed to be <b>idempotent</b> (Redis Sets, repeated {@code expire}).</li>
 *   <li>Tokens/blocks with <i>non-positive</i> remaining TTL are skipped.</li>
 *   <li>Partial failures from Redis are allowed to bubble up as unchecked exceptions; the caller may retry later.</li>
 *   <li>On successful completion the in-memory store is cleared via {@link InMemoryTokenStoreStrategy#clearMigrated()}.</li>
 * </ul>
 *
 * <h3>Concurrency</h3>
 * <p>Reads a live view of the in-memory maps; concurrent updates may interleave. Repeated invocations are safe:
 * already-migrated entries will be no-ops in Redis and will be cleared from memory at the end.</p>
 *
 * @author RsLan
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class TokenMigrationService {

    private final InMemoryTokenStoreStrategy inMem;
    private final StringRedisTemplate redis;

    /**
     * Prefix for Redis keys that mark a user as temporarily blocked.
     * Example final key: {@code blocked:<userId>}.
     */
    @Value("${prefix.blocked}")
    private String blockedPrefix;

    /**
     * @return {@code true} if there is nothing to migrate (neither tokens nor block flags).
     */
    public boolean isInMemoryEmpty() {
        return inMem.getByUser().isEmpty() && inMem.getBlacklistUntil().isEmpty();
    }

    /**
     * Migrates all pending in-memory blocks and tokens to Redis, preserving remaining TTLs.
     * <p>
     * Steps:
     * <ol>
     *   <li>For each entry in {@code blacklistUntil}: compute remaining TTL and set {@code blockedPrefix+userId} in Redis.</li>
     *   <li>For each token per user in {@code byUser}: compute remaining TTL and:
     *     <ul>
     *       <li>add {@code userId} to Redis Set keyed by {@code token};</li>
     *       <li>apply {@code expire(token, TTL)};</li>
     *       <li>add {@code token} to the user's index Set keyed by {@code userId.toString()}.</li>
     *     </ul>
     *   </li>
     *   <li>Clear the in-memory storage via {@link InMemoryTokenStoreStrategy#clearMigrated()}.</li>
     * </ol>
     * </p>
     *
     * @throws RuntimeException if Redis operations fail (caller is expected to retry later)
     */
    public void migrateToRedis() {
        final ConcurrentHashMap<UUID, ConcurrentHashMap<String, Long>> byUser = inMem.getByUser();
        final ConcurrentHashMap<UUID, Long> blacklistUntil = inMem.getBlacklistUntil();

        long now = System.currentTimeMillis();

        // 1) migrate block flags
        for (Map.Entry<UUID, Long> e : blacklistUntil.entrySet()) {
            UUID userId = e.getKey();
            long liveTime = e.getValue();
            long ttlMs = liveTime - now;
            if (ttlMs > 0) {
                redis.opsForValue().set(blockedPrefix + userId, "blocked", Duration.ofMillis(ttlMs));
            }
        }

        // 2) migrate tokens and user index
        for (Map.Entry<UUID, ConcurrentHashMap<String, Long>> e : byUser.entrySet()) {
            UUID userId = e.getKey();
            ConcurrentHashMap<String, Long> tokens = e.getValue();

            for (Map.Entry<String, Long> t : tokens.entrySet()) {
                String token = t.getKey();
                long liveTime = t.getValue();
                long ttlMs = liveTime - now;
                if (ttlMs > 0) {
                    // token key (existence == token validity)
                    redis.opsForSet().add(token, userId.toString());
                    redis.expire(token, Duration.ofMillis(ttlMs));

                    // user index
                    redis.opsForSet().add(userId.toString(), token);
                }
            }
        }

        // 3) clear local copies after successful migration
        inMem.clearMigrated();
    }
}
