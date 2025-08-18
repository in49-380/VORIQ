package com.voriq.parser_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CarDto {
    private Long id;
    private ModelDto model;
    private EngineDto engine;
    private FuelTypeDto fuelType;
    private String pictureCar;
}
