package com.voriq.security_service.controller;

import com.voriq.security_service.controller.API.TokenAPI;
import com.voriq.security_service.domain.dto.TokenRequestDto;
import com.voriq.security_service.domain.dto.TokensDto;
import com.voriq.security_service.service.interfaces.TokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenController implements TokenAPI {

    TokenService tokenService;

    @Override
    public ResponseEntity<TokensDto> issue(TokenRequestDto dto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(tokenService.createTokens(dto));
    }
}
