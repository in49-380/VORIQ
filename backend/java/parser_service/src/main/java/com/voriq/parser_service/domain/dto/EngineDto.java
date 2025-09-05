package com.voriq.parser_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class EngineDto {

    private Long id;
    private String type;
    private FuelTypeDto fuelType;
    private String seriesCode;
    private String engineCode;
    private Integer displacementCC;
}
