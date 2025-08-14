package com.voriq.security_service.service.TokenStoreStrategy;

import com.voriq.security_service.exception_handler.exception.AccessDeniedException;
import com.voriq.security_service.service.interfaces.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

import static com.voriq.security_service.service.TokenStoreStrategy.DelegatingTokenStoreStrategy.DEFAULT_SET_VALUE;
import static com.voriq.security_service.service.token_utilities.TokenUtilities.isUuid;

/**
 * Primary {@link TokenStoreStrategy} backed by Redis.
 *
 * <h3>Key model</h3>
 * <ul>
 *   <li><b>Token key</b>: the token value itself is used as a Redis key.
 *       It is created as a Set that contains the owner {@code userId} and has a TTL = {@code accessExpirationMs}.
 *       Existence of this key means the token is valid (checked via {@link #isValid(String)}).</li>
 *   <li><b>User index</b>: the {@code userId.toString()} key stores a Set of token keys issued for that user.
 *       It has <i>no TTL</i> and serves as an index for cleanup and revocation.</li>
 * </ul>
 *
 * <h3>Behavior</h3>
 * <ul>
 *   <li>{@link #isApplicable()} pings Redis (returns {@code true} if reachable).</li>
 *   <li>{@link #saveToken(String, UUID)}:
 *     <ol>
 *       <li>removes expired tokens from the user's index (by checking token-key existence),</li>
 *       <li>enforces the per-user session limit (see {@code token.max-tokens}); if exceeded, all sessions are revoked
 *           and the user is temporarily blocked via {@link BlockService},</li>
 *       <li>creates/updates the token key with TTL and adds it to the user's index set.</li>
 *     </ol>
 *   </li>
 *   <li>{@link #isValid(String)} returns {@code true} iff the token key exists.</li>
 *   <li>{@link #revokeAll(UUID)} deletes all token keys from the user's index and the index itself.</li>
 * </ul>
 *
 * <h3>Exceptions</h3>
 * <ul>
 *   <li>{@link AccessDeniedException} — when the user is currently blocked or the limit policy denies new sessions.</li>
 *   <li>Connectivity/infra issues should bubble up as unchecked exceptions for the delegator to fallback.</li>
 * </ul>
 *
 * <h3>Configuration</h3>
 * <ul>
 *   <li>{@code token.access-token-expiration-ms} — token TTL in milliseconds (applied to token keys).</li>
 *   <li>{@code token.max-tokens} — maximum simultaneous tokens per user (enforced via user index).</li>
 * </ul>
 *
 * @author RsLan
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
@Order(1)
public class RedisTokenStoreStrategy implements TokenStoreStrategy {

    @Value("${token.access-token-expiration-ms}")
    private long accessExpirationMs;

    @Value("${token.max-tokens}")
    private int maxTokens;

    private final BlockService blockService;
    private final StringRedisTemplate redisTemplate;

    /**
     * Checks if Redis is reachable by issuing a {@code PING}.
     *
     * @return {@code true} if Redis replies {@code PONG}; {@code false} otherwise
     */
    @Override
    public boolean isApplicable() {
        try {
            var conn = redisTemplate.getConnectionFactory().getConnection();
            return "PONG".equals(conn.ping());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Stores a token for the user, enforcing session limits and cleanup.
     *
     * <p>Flow: cleanup expired tokens from the user index → enforce limit (may revoke & block) → create
     * token key with TTL and link it from the user index.</p>
     *
     * @param token  token value
     * @param userId owner id
     * @throws AccessDeniedException if the user is blocked or new sessions are temporarily disallowed
     */
    @Override
    public void saveToken(String token, UUID userId) {
        cleanupExpiredTokens(userId);
        enforceSessionLimit(userId);

        String idxKey = userId.toString();

        // create token key with TTL by storing owner in a Set (existence == validity)
        redisTemplate.opsForSet().add(token, idxKey);
        redisTemplate.expire(token, Duration.ofMillis(accessExpirationMs));

        // link token from user index
        redisTemplate.opsForSet().add(idxKey, token);
    }

    /**
     * Token is considered valid iff its key exists in Redis (i.e., the TTL has not expired yet).
     *
     * <p>Semantics:</p>
     * <ul>
     *   <li>Uses {@code EXISTS} via {@link org.springframework.data.redis.core.RedisTemplate#hasKey(Object)}.</li>
     *   <li>No destructive/read-modify side effects.</li>
     * </ul>
     *
     * @param token token key to check (must not be {@code null})
     * @return {@code true} if the token key exists; {@code false} otherwise
     * @throws RuntimeException if a Redis access error occurs (e.g., connection issues).
     *                          (Spring Data may throw a {@code DataAccessException}, which is a {@code RuntimeException}.)
     */
    @Override
    public boolean isValid(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }

    /**
     * Retrieves a single (random) member from the Redis Set stored under the given {@code key}.
     *
     * <p>Semantics:</p>
     * <ul>
     *   <li>First validates the key format; non-UUID keys yield {@link DelegatingTokenStoreStrategy#DEFAULT_SET_VALUE}.</li>
     *   <li>Uses {@code SRANDMEMBER} (non-destructive) via {@link org.springframework.data.redis.core.SetOperations#randomMember(Object)}.</li>
     *   <li>If the returned member is a valid UUID string, it is returned as-is; otherwise
     *       {@link DelegatingTokenStoreStrategy#DEFAULT_SET_VALUE} is returned.</li>
     *   <li>Backend (Redis) access errors are swallowed and mapped to
     *       {@link DelegatingTokenStoreStrategy#DEFAULT_SET_VALUE}.</li>
     * </ul>
     *
     * @param key Redis key expected to point to a Set (must not be {@code null}); only UUID-formatted keys are accepted
     * @return a UUID string from the Set if present and valid; otherwise {@link DelegatingTokenStoreStrategy#DEFAULT_SET_VALUE}
     */
    @Override
    public String getSetValueByKey(String key) {
        if (!isUuid(key)) return DEFAULT_SET_VALUE;

        try {
            String value = redisTemplate.opsForSet().randomMember(key);
            return isUuid(value) ? value : DEFAULT_SET_VALUE;
        } catch (DataAccessException e) {
            return DEFAULT_SET_VALUE;
        }
    }

    /**
     * Deletes all token keys referenced by the user's index and removes the index itself.
     *
     * @param userId user whose tokens to revoke
     */
    public void revokeAll(UUID userId) {
        String idxKey = userId.toString();
        Set<String> tokens = redisTemplate.opsForSet().members(idxKey);
        if (tokens == null || tokens.isEmpty()) return;

        for (String t : tokens) {
            redisTemplate.delete(t);
        }
        redisTemplate.delete(idxKey);
    }

    /**
     * Removes from the user index all tokens whose token key no longer exists (expired/evicted).
     *
     * @param userId user whose index to clean
     */
    private void cleanupExpiredTokens(UUID userId) {
        String idxKey = userId.toString();
        Set<String> tokens = redisTemplate.opsForSet().members(idxKey);
        if (tokens == null || tokens.isEmpty()) return;

        for (String t : tokens) {
            boolean exists = Boolean.TRUE.equals(redisTemplate.hasKey(t));
            if (!exists) {
                redisTemplate.opsForSet().remove(idxKey, t);
            }
        }
    }

    /**
     * Enforces the per-user session limit and blocking policy.
     * <ul>
     *   <li>If the user is currently blocked, clears the index and throws {@link AccessDeniedException}.</li>
     *   <li>If the number of active tokens in the index is {@code >= maxTokens}, revokes all and blocks the user.</li>
     * </ul>
     *
     * @param userId user to check
     * @throws AccessDeniedException if the user is blocked
     */
    private void enforceSessionLimit(UUID userId) {
        if (blockService.isBlocked(userId)) {
            redisTemplate.delete(userId.toString());
            throw new AccessDeniedException("The active session limit has been exceeded. New sessions are temporarily unavailable.");
        }
        String idxKey = userId.toString();

        Long active = redisTemplate.opsForSet().size(idxKey);
        if (active != null && active >= maxTokens) {
            revokeAll(userId);
            blockService.block(userId);
        }
    }
}
