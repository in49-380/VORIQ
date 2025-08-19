package com.voriq.security_service.service;

import com.voriq.security_service.domain.dto.TokenRequestDto;
import com.voriq.security_service.domain.dto.TokensDto;
import com.voriq.security_service.exception_handler.exception.*;
import com.voriq.security_service.repository.UserRepository;
import com.voriq.security_service.service.TokenStoreStrategy.TokenStoreStrategy;
import com.voriq.security_service.service.interfaces.TokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Token issuing service implementation.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Validate that the requesting user exists and the provided key matches the stored one.</li>
 *   <li>Generate a new access token and persist it using the configured {@link TokenStoreStrategy}
 *       (Redis as primary, in-memory as fallback via delegator).</li>
 * </ul>
 *
 * <h3>Error handling</h3>
 * <ul>
 *   <li>{@link UserNotFoundException} — when user is absent or the provided key does not match.</li>
 *   <li>{@link ServiceUnavailableException} — wraps infrastructure/database failures while resolving the user.</li>
 * </ul>
 *
 * <p>Thread-safety: the service is stateless; underlying store/repository components must be thread-safe.</p>
 *
 * @author RsLan
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenServiceImpl implements TokenService {

    UserRepository repository;
    TokenStoreStrategy tokenStoreStrategy;

    /**
     * Creates and persists a new access token for the given request.
     *
     * <p>Flow:</p>
     * <ol>
     *   <li>Resolve the stored key for the {@code userId} (may throw {@link ServiceUnavailableException}).</li>
     *   <li>Compare with the provided {@code key}; if mismatch or absent — throw {@link UserNotFoundException}.</li>
     *   <li>Generate a random access token and save it via {@link TokenStoreStrategy}.</li>
     * </ol>
     *
     * @param dto request containing {@code userId} and {@code key}
     * @return DTO with the newly generated access token
     * @throws UserNotFoundException       if user does not exist or key mismatch
     * @throws ServiceUnavailableException if user resolution fails due to backend issues
     * @throws RuntimeException            if the token store backend is unavailable (delegator may fallback)
     */
    @Override
    public TokensDto createTokens(TokenRequestDto dto) {

        UUID id = dto.getUserId();

        UUID requestedKey = getKeyByUserIdOrFail(id);
        if (requestedKey == null || !requestedKey.equals(dto.getKey())) {
            throw new UserNotFoundException(id, dto.getKey());
        }
        TokensDto tokensDto = TokensDto.builder()
                .accessToken(UUID.randomUUID().toString())
                .build();

        tokenStoreStrategy.saveToken(tokensDto.getAccessToken(), id);

        return tokensDto;
    }

    /**
     * Validates the provided access token (expected as a UUID string).
     *
     * <p>Flow:</p>
     * <ol>
     *   <li>Verify that {@code token} is a valid UUID string; if not, throw {@link BadRequestException}.</li>
     *   <li>Delegate validation to {@link TokenStoreStrategy#isValid(String)}.</li>
     *   <li>If the token is not valid, throw {@link UnauthorizedException}.</li>
     * </ol>
     *
     * <p>Notes:</p>
     * <ul>
     *   <li>The raw token must not be logged; mask it if needed.</li>
     *   <li>Backend/IO issues during validation may bubble up as {@code 5xx} via the underlying strategy.</li>
     * </ul>
     *
     * @param token access token to validate (UUID string)
     * @throws BadRequestException         if the token has an invalid format (not a UUID)
     * @throws UnauthorizedException       if the token is invalid, expired, or revoked
     * @throws ServiceUnavailableException if validation cannot be performed due to backend issues
     * @throws RuntimeException            if the token-store backend fails unexpectedly
     */

    @Override
    public void validateToken(String token) {
        try {
            UUID.fromString(token);
        } catch (Exception e) {
            throw new BadRequestException("Token format is wrong.");
        }
        if (!tokenStoreStrategy.isValid(token))
            throw new UnauthorizedException("Token is invalid.");
    }

    /**
     * Revokes the provided access token (expected as a UUID string).
     *
     * <p>Flow:</p>
     * <ol>
     *   <li>Verify that {@code token} is a valid UUID string; if not, throw {@link BadRequestException}.</li>
     *   <li>Call {@link #validateToken(String)} to ensure the token currently exists and is valid.</li>
     *   <li>Delegate revocation to {@link TokenStoreStrategy#revokeToken(String)}.</li>
     *   <li>If the strategy reports failure (e.g., token absent/already revoked), throw {@link ServerException}.</li>
     * </ol>
     *
     * <p>Notes:</p>
     * <ul>
     *   <li>The raw token must not be logged; mask it if needed.</li>
     *   <li>Operation is intended to be idempotent from the caller’s perspective; repeated calls may result in
     *       {@link ServerException} if the token is already revoked/unknown, depending on strategy behavior.</li>
     *   <li>Backend/IO issues during revocation may bubble up as {@code 5xx} (e.g., {@link ServiceUnavailableException}).</li>
     * </ul>
     *
     * @param token access token to revoke (UUID string)
     * @throws BadRequestException         if the token has an invalid format (not a UUID)
     * @throws UnauthorizedException       if the token is invalid, expired, or already revoked (via {@link #validateToken(String)})
     * @throws ServerException        if a valid token could not be revoked
     * @throws ServiceUnavailableException if revocation cannot be performed due to backend issues
     * @throws RuntimeException            if the token-store backend fails unexpectedly
     */

    @Override
    public void revokeToken(String token) {
        validateToken(token);
        boolean isRevoked = tokenStoreStrategy.revokeToken(token);
        if (!isRevoked) {
            throw new ServerException("The token could not be revoked. Try again later.");
        }
    }

    /**
     * Fetches the stored key for the given user id.
     *
     * <p>Returns {@code null} if the user is not found. Any infrastructure/DB error is wrapped into
     * {@link ServiceUnavailableException} for consistent HTTP 503 mapping by the global handler.</p>
     *
     * @param id user id to resolve
     * @return stored key or {@code null} if user not found
     * @throws ServiceUnavailableException if repository access fails
     */
    public UUID getKeyByUserIdOrFail(UUID id) {
        try {
            return repository.findKeyOnlyByUserId(id);
        } catch (Exception ex) {
            throw new ServiceUnavailableException(
                    "The server is currently overloaded or under maintenance. Please try again later.", ex);
        }
    }
}
