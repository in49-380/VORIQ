package com.voriq.security_service.service;

import com.voriq.security_service.service.interfaces.BlockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

/**
 * Redis-backed implementation of {@link BlockService}.
 *
 * <p>Uses a dedicated Redis key to mark a user as temporarily blocked.
 * The key is created with a TTL equal to the access token lifetime.</p>
 *
 * <h3>Key format</h3>
 * <ul>
 *   <li><code>{prefix.blocked}{userId}</code>, e.g. <code>blocked:1111-...</code></li>
 *   <li>Value is a simple marker string (e.g., <code>"blocked"</code>)</li>
 *   <li>TTL = <code>token.access-token-expiration-ms</code></li>
 * </ul>
 *
 * <h3>Concurrency & semantics</h3>
 * <ul>
 *   <li>Blocking is idempotent: repeated calls reset/extend the TTL.</li>
 *   <li>{@link #isBlocked(UUID)} checks key existence; unblocking happens automatically via TTL expiry.</li>
 * </ul>
 *
 * @author RsLan
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class BlockServiceImpl implements BlockService {

    @Value("${token.access-token-expiration-ms}")
    private long accessExpirationMs;

    @Value("${prefix.blocked}")
    private String blockedPrefix;

    private final StringRedisTemplate redis;

    /**
     * Blocks the given user by writing a marker key to Redis with a TTL equal to the access-token expiration.
     *
     * <p>Semantics:</p>
     * <ul>
     *   <li>Key: {@code getKey(userId)}; Value: {@code "blocked"}.</li>
     *   <li>TTL: {@code accessExpirationMs} milliseconds.</li>
     *   <li>Returns {@code true} if the Redis write succeeds; {@code false} if any Redis error occurs.</li>
     *   <li>Idempotent with respect to value; repeated calls refresh (reset) the TTL.</li>
     * </ul>
     *
     * <p>Notes:</p>
     * <ul>
     *   <li>Exceptions from the Redis client are caught and not propagated.</li>
     *   <li>Callers may apply a fallback (e.g., in-memory block) when {@code false} is returned.</li>
     * </ul>
     *
     * @param userId identifier of the user to block (must not be {@code null})
     * @return {@code true} on successful write; {@code false} on failure
     */

    @Override
    public boolean block(UUID userId) {
        try{
            redis.opsForValue().set(getKey(userId), "blocked", Duration.ofMillis(accessExpirationMs));
            return true;
        }
        catch (Exception ex){
            return false;
        }
    }

    /**
     * @param userId user to check
     * @return {@code true} if the block key exists; {@code false} otherwise
     */
    @Override
    public boolean isBlocked(UUID userId) {
        return Boolean.TRUE.equals(redis.hasKey(getKey(userId)));
    }

    private String getKey(UUID userId) {
        return blockedPrefix + userId;
    }
}
