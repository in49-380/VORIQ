package com.voriq.parser_service.exception_handler.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response with errors information ")
public class ErrorResponse {

    @Schema(description = "The time of the appearance of an error", example = "2025-08-28T22:25:52")
    private LocalDateTime timestamp;

    @Schema(description = "Error http status", example = "404")
    private int status;

    @Schema(description = "Description of status", example = "Not Found")
    private String error;

    @Schema(description = "Errors message", example = "Object with this ID was not found.")
    private String message;

    @Schema(description = "The path when contacting which an error arose", example = "/api/v1/cars/100")
    private String path;
}
