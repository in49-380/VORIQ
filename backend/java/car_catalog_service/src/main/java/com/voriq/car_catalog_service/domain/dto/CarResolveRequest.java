package com.voriq.car_catalog_service.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Schema(name = "Car resolve request")
public class CarResolveRequest {

    @Schema(name = "Brand ID", example = "23")
    @NotNull(message = "Brand Id can not be null")
    @Min(value = 1, message = "Id must be great of 0")
    private Long brandId;

    @Schema(name = "Model ID", example = "12")
    @NotNull(message = "Model Id can not be null")
    @Min(value = 1, message = "Id must be great of 0")
    private Long modelId;

    @Schema(name = "Year ID", example = "5")
    @NotNull(message = "Year Id can not be null")
    @Min(value = 1, message = "Id must be great of 0")
    private Long yearId;

    @Schema(name = "Engine ID", example = "9")
    @NotNull(message = "Engine Id can not be null")
    @Min(value = 1, message = "Id must be great of 0")
    private Long engineId;

    @Schema(name = "Engine ID", example = "3")
    @NotNull(message = "Transmission Id can not be null")
    @Min(value = 1, message = "Id must be great of 0")
    private Long transmissionId;

    @Schema(name = "Engine ID", example = "1")
    @NotNull(message = "Drive layout Id can not be null")
    @Min(value = 1, message = "Id must be great of 0")
    private Long driveLayout;
}
