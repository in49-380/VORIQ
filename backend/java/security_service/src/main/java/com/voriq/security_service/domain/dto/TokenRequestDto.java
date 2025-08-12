package com.voriq.security_service.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "Request data", description = "User data")
public class TokenRequestDto {

    @Schema(description = "User id", example = "11111111-1111-1111-1111-111111111111")
    @NotNull(message = "User id cannot be null")
    private UUID userId;

    @Schema(description = "User key", example = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
    @NotNull(message = "User key cannot be null")
    private UUID key;
}
