package com.voriq.parser_service.domain.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EngineDto {

    private Long id;
    private String type;
    private FuelTypeDto fuelType;
    private String seriesCode;
    private String engineCode;
    private Integer displacementCC;
}
