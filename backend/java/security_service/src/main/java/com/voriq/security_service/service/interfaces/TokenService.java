package com.voriq.security_service.service.interfaces;

import com.voriq.security_service.domain.dto.TokenRequestDto;
import com.voriq.security_service.domain.dto.TokensDto;

public interface TokenService {

    TokensDto createTokens(TokenRequestDto dto);
}
