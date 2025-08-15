package com.voriq.security_service.service.TokenStoreStrategy;

import com.voriq.security_service.exception_handler.exception.AccessDeniedException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.voriq.security_service.service.TokenStoreStrategy.DelegatingTokenStoreStrategy.DEFAULT_SET_VALUE;
import static com.voriq.security_service.service.token_utilities.TokenUtilities.isUuid;

/**
 * In-memory fallback implementation of {@link TokenStoreStrategy}.
 *
 * <p>This strategy keeps per-user tokens and their expirations in local concurrent maps.
 * It is intended to be used when the primary backend (e.g., Redis) is unavailable.
 * When the primary backend becomes available again, accumulated data may be migrated and
 * this store can be cleared via {@link #clearMigrated()}.</p>
 *
 * <h3>Behavior</h3>
 * <ul>
 *   <li>Each saved token is associated with an expiration timestamp
 *       (configured by {@code token.access-token-expiration-ms}).</li>
 *   <li>On each save, expired tokens for the user are cleaned up.</li>
 *   <li>If the number of active tokens for a user reaches {@code token.max-tokens},
 *       all user tokens are revoked and the user is temporarily <em>blocked</em>
 *       for the same duration as the access token TTL.</li>
 *   <li>{@link #isValid(String)} returns {@code false} for unknown/expired tokens,
 *       and lazily purges expired entries.</li>
 * </ul>
 *
 * <h3>Thread-safety</h3>
 * <p>Backed by {@link ConcurrentHashMap}s; operations are designed for concurrent use.
 * Note that checks like "size vs. limit" are approximate under concurrency; the strategy
 * opts for simplicity over strict global consistency.</p>
 *
 * <h3>Exceptions</h3>
 * <ul>
 *   <li>{@link AccessDeniedException} is thrown when attempting to save a token
 *       for a currently blocked user.</li>
 * </ul>
 *
 * <h3>Configuration</h3>
 * <ul>
 *   <li>{@code token.access-token-expiration-ms} – access token TTL (milliseconds).</li>
 *   <li>{@code token.max-tokens} – max simultaneous tokens per user before block &amp; revoke.</li>
 * </ul>
 *
 * @author RsLan
 * @since 1.0.0
 */
@Component
@NoArgsConstructor
@Getter
@Order(2)
public class InMemoryTokenStoreStrategy implements TokenStoreStrategy {

    @Getter
    private final ConcurrentHashMap<UUID, ConcurrentHashMap<String, Long>> byUser = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> tokenExpiry = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, UUID> tokenToUser = new ConcurrentHashMap<>();
    @Getter
    private final ConcurrentHashMap<UUID, Long> blacklistUntil = new ConcurrentHashMap<>();

    @Value("${token.access-token-expiration-ms}")
    private long accessExpirationMs;

    @Value("${token.max-tokens}")
    private int maxTokens;

    /**
     * This fallback is always deemed applicable.
     *
     * @return {@code true}
     */
    @Override
    public boolean isApplicable() {
        return true;
    }

    /**
     * Saves (registers) a token for the given user.
     *
     * <p>Flow:
     * <ol>
     *   <li>Reject if user is currently blocked ({@link AccessDeniedException}).</li>
     *   <li>Cleanup expired tokens for the user.</li>
     *   <li>If active token count reached {@code maxTokens}, revoke all and block user
     *       for {@code accessExpirationMs}.</li>
     *   <li>Otherwise, store the token with its expiration timestamp.</li>
     * </ol>
     * </p>
     *
     * @param token  token value (non-null)
     * @param userId token owner
     * @throws AccessDeniedException if the user is temporarily blocked
     */
    @Override
    public void saveToken(String token, UUID userId) {
        long now = System.currentTimeMillis();

        checkBlockedUser(userId, now);
        cleanupExpiredForUser(userId, now);
        checkLimitTokens(userId, now);

        long expiresAt = now + accessExpirationMs;
        byUser.computeIfAbsent(userId, __ -> new ConcurrentHashMap<>()).put(token, expiresAt);
        tokenExpiry.put(token, expiresAt);
        tokenToUser.put(token, userId);
    }

    /**
     * Checks whether a token is currently valid (known and not expired).
     * <p>
     * Semantics:
     * <ul>
     *   <li>Looks up absolute expiration time (epoch millis) in {@code tokenExpiry}.</li>
     *   <li>If no entry exists → returns {@code false}.</li>
     *   <li>If {@code exp < now} → lazily purges the entry via {@link #expireToken(String, long)} and returns {@code false}.</li>
     *   <li>Otherwise returns {@code true}.</li>
     * </ul>
     * </p>
     *
     * <p>Side effects: expired entries are removed on read (“lazy purge”).</p>
     *
     * @param token token to check (must not be {@code null})
     * @return {@code true} if the token exists and has not expired; {@code false} otherwise
     */
    @Override
    public boolean isValid(String token) {
        long now = System.currentTimeMillis();
        Long exp = tokenExpiry.get(token);
        if (exp == null) return false;
        if (exp < now) {
            expireToken(token, now);
            return false;
        }
        return true;
    }

    /**
     * Returns the user identifier associated with the given key, or {DEFAULT_SET_VALUE}
     * if the key is not a UUID or there is no mapping.
     *
     * <p>Semantics:</p>
     * <ul>
     *   <li>Validates the key via {@code isUuid(key)}; non-UUID keys yield {DEFAULT_SET_VALUE}.</li>
     *   <li>Performs a non-destructive lookup in {@code tokenToUser} (does not modify the store).</li>
     *   <li>If a mapping exists, returns the {@code UUID} as a string; otherwise returns {DEFAULT_SET_VALUE}.</li>
     * </ul>
     *
     * @param key logical key to look up (expected token; must not be {@code null})
     * @return the associated user id as a string, or {DEFAULT_SET_VALUE} if absent/invalid
     */
    @Override
    public String getSetValueByKey(String key) {
        if (!isUuid(key)) return DEFAULT_SET_VALUE;

        UUID userId = tokenToUser.get(key);
        return (userId != null) ? userId.toString() : DEFAULT_SET_VALUE;
    }

    /**
     * Clears all in-memory state. Intended to be called after a successful migration
     * to the primary store to release memory and avoid duplicate data.
     */
    public void clearMigrated() {
        byUser.clear();
        tokenExpiry.clear();
        tokenToUser.clear();
        blacklistUntil.clear();
    }

    /* ===== Internal helpers ===== */

    private void revokeAll(UUID userId) {
        var map = byUser.remove(userId);
        if (map == null || map.isEmpty()) return;
        Set<String> tokens = map.keySet();
        for (String t : tokens) {
            tokenExpiry.remove(t);
            tokenToUser.remove(t);
        }
    }

    private void checkBlockedUser(UUID userId, long now) {
        if (isBlocked(userId, now)) {
            long secs = getRemainingBlockSeconds(userId, now);
            throw new AccessDeniedException("User is temporarily blocked. Try again in " + secs + "s");
        }
    }

    private void cleanupExpiredForUser(UUID userId, long now) {
        var map = byUser.get(userId);
        if (map == null || map.isEmpty()) return;

        Iterator<Map.Entry<String, Long>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Long> e = it.next();
            String token = e.getKey();
            long exp = e.getValue();
            if (exp < now) {
                it.remove();
                tokenExpiry.remove(token);
                tokenToUser.remove(token);
            }
        }
        if (map.isEmpty()) {
            byUser.remove(userId);
        }
    }

    private void checkLimitTokens(UUID userId, long now) {
        int active = byUser.getOrDefault(userId, new ConcurrentHashMap<>()).size();
        if (active >= maxTokens) {
            revokeAll(userId);
            blacklistUntil.put(userId, now + accessExpirationMs);
        }
    }

    private void expireToken(String token, long now) {
        tokenExpiry.remove(token);
        UUID uid = tokenToUser.remove(token);
        if (uid != null) {
            var map = byUser.get(uid);
            if (map != null) {
                map.remove(token);
                if (map.isEmpty()) byUser.remove(uid);
            }
        }
    }

    private boolean isBlocked(UUID userId, long now) {
        Long until = blacklistUntil.get(userId);
        if (until == null) return false;
        if (until <= now) {
            blacklistUntil.remove(userId);
            return false;
        }
        return true;
    }

    private long getRemainingBlockSeconds(UUID userId, long now) {
        Long until = blacklistUntil.get(userId);
        return (until == null || until <= now) ? 0 : (until - now + 999) / 1000;
    }
}
