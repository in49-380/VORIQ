package com.voriq.security_service.domain.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Schema(name = "Tokens dto", description = "Return new tokens")
public class TokensDto {

    @Schema(description = "Access token", example = "d3cc8ac7-38d6-4f6c-83c8-ecf37c843e8a")
    private String accessToken;
}
