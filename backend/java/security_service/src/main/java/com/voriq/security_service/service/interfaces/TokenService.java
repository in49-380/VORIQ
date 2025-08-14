package com.voriq.security_service.service.interfaces;

import com.voriq.security_service.domain.dto.TokenRequestDto;
import com.voriq.security_service.domain.dto.TokensDto;

import java.util.UUID;

public interface TokenService {

    TokensDto createTokens(TokenRequestDto dto);

    void validateToken(String token);
}
