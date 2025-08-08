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
     *
     * @throws com.voriq.security_service.exception_handler.exception.RestException
     *         if business rules are violated (blocked user, max active tokens, etc.)
     * @throws RuntimeException
     *         if an infrastructure error occurs (e.g., backend is unavailable)
     */
    void saveToken(String token, UUID userId);

    /**
     * Checks whether the provided token is currently valid/active.
     *
     * @param token token to check
     * @return {@code true} if the token is known and valid; {@code false} otherwise
     *
     * @throws RuntimeException
     *         if an infrastructure error occurs while accessing the backend
     */
    boolean isValid(String token);
}
