package com.voriq.security_service.controller.API;

import com.voriq.security_service.config.annotation.bearer_token.BearerToken;
import com.voriq.security_service.domain.dto.TokenRequestDto;
import com.voriq.security_service.domain.dto.TokensDto;
import com.voriq.security_service.exception_handler.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/v1/tokens")
@Tag(name = "Token controller", description = "Controller for  issuing, validating, and revoking access tokens")
public interface TokenAPI {

    @Operation(
            summary = "Token issuance",
            description = "Issuing a token to an active user by ID.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TokenRequestDto.class)))
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful issuing",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TokensDto.class))}
            ),
            @ApiResponse(responseCode = "400",
                    description = "Bad request.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "403",
                    description = "The user does not have access.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "404",
                    description = "User not found.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "429",
                    description = "Too many requests. Try again later.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "500",
                    description = "Temporary service error.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "503",
                    description = "The server is currently overloaded or under maintenance. Please try again later.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))}
    )
    @PostMapping("/issue")
    ResponseEntity<TokensDto> issue(
            @Valid
            @org.springframework.web.bind.annotation.RequestBody
            TokenRequestDto dto);


    @Operation(
            summary = "Token validator",
            description = "Validates the bearer token received in the header."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Successful validation"
            ),
            @ApiResponse(responseCode = "400",
                    description = "Bad request.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "401",
                    description = "Invalid token",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "429",
                    description = "Too many requests. Try again later.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "500",
                    description = "Temporary service error.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))}
    )
    @GetMapping("/validate")
    ResponseEntity<Void> validate(
            @BearerToken
            @Parameter(hidden = true)
            @NotNull(message = "Bearer token cannot be null")
            String token);

    @Operation(
            summary = "Token revoker",
            description = "Revoke the bearer token received in the header."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Successful revoke"
            ),
            @ApiResponse(responseCode = "400",
                    description = "Bad request.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "401",
                    description = "Invalid token",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "500",
                    description = "Temporary service error.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))}
    )
    @DeleteMapping("/revoke")
    ResponseEntity<Void> revoke(
            @BearerToken
            @Parameter(hidden = true)
            @NotNull(message = "Bearer token cannot be null")
            String token);
}
