package com.voriq.security_service.service.TokenStoreStrategy;

import java.util.UUID;

/**
 * Contract for token storage backends (e.g., primary Redis and fallback in-memory).
 * <p>
 * Implementations must be thread-safe and operations should be idempotent where applicable.
 * The delegating strategy will call {@link #isApplicable()} to pick the first available backend.
 * </p>
 *
 * <h3>Error handling</h3>
 * <ul>
 *   <li><b>Business rule violations</b> (e.g., user blocked, max tokens exceeded) should throw
 *   {@link com.voriq.security_service.exception_handler.exception.RestException} (unchecked).</li>
 *   <li><b>Infrastructure/Connectivity failures</b> (e.g., Redis down) should throw unchecked
 *   runtime exceptions; the delegator may fallback to another strategy.</li>
 * </ul>
 *
 * @author RsLan
 * @since 1.0.0
 */
public interface TokenStoreStrategy {

    /**
     * Indicates whether this strategy can be used right now (e.g., backend is reachable and healthy).
     *
     * @return {@code true} if the strategy is currently applicable; {@code false} otherwise
     */
    boolean isApplicable();

    /**
     * Persist (register) the token for the given user.
     * <p>
     * Implementations should be idempotent for the same ({@code userId}, {@code token}) pair.
     * </p>
     *
     * @param token  a non-null token string
     * @param userId the owner's UUID
     * @throws com.voriq.security_service.exception_handler.exception.RestException if business rules are violated (blocked user, max active tokens, etc.)
     * @throws RuntimeException                                                     if an infrastructure error occurs (e.g., backend is unavailable)
     */
    void saveToken(String token, UUID userId);

    /**
     * Checks whether the provided token is currently valid/active.
     *
     * @param token token to check
     * @return {@code true} if the token is known and valid; {@code false} otherwise
     * @throws RuntimeException if an infrastructure error occurs while accessing the backend
     */
    boolean isValid(String token);

    /**
     * Retrieves a single member from the set-like collection stored under the given {@code key}
     * (e.g., a Redis Set), without modifying the collection.
     *
     * <p>Semantics:</p>
     * <ul>
     *   <li>Non-destructive: the returned value is not removed from the set.</li>
     *   <li>If multiple members exist, the selection is implementation-defined (e.g., first/any/random).</li>
     *   <li>If the key does not exist or the set is empty, returns {@code null}.</li>
     * </ul>
     *
     * @param key non-null logical key identifying the set/collection
     * @return a set member if present; {@code null} if absent or empty
     * @throws RuntimeException if an infrastructure/backend error occurs while accessing the storage
     */
    String getSetValueByKey(String key);

    /**
     * Revokes (invalidates) the given token in the underlying store.
     *
     * <p>Semantics:</p>
     * <ul>
     *   <li>If the token exists, it is removed/blacklisted and any auxiliary mappings
     *       (e.g., expiry entries, per-user indexes) are cleaned up as defined by the implementation.</li>
     *   <li>If the token does not exist, no changes are made and {@code false} is returned.</li>
     *   <li>Idempotent: repeated calls for the same token after a successful revocation return {@code false}.</li>
     * </ul>
     *
     * @param token non-null token identifier to revoke
     * @return {@code true} if the token existed and was revoked; {@code false} otherwise
     * @throws RuntimeException if an infrastructure/backend error occurs while accessing the storage
     */
    boolean revokeToken(String token);
}
