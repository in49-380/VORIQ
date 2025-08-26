package com.voriq.car_catalog_service.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Schema(description = "Car response")
public class CarResponseDto {

    @Schema(description = "Сar id in the catalog.", example = "34")
    private Long id;

    @Schema(description = "Model of car.", example = "Q5")
    private String model;

    @Schema(description = "Type of engine.", example = "ICE")
    private String engine;

    @Schema(description = "Type of fuel.", example = "diesel")
    private String fuelType;

    @Schema(description = "Car brand.", example = "Audi")
    private String brand;

    @Schema(description = "Year of car production.", example = "2022")
    private int year;
}
