package com.voriq.car_catalog_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CarResolveRequest {

    private Long brandId;
    private Long modelId;
    private Long yearId;
    private Long engineId;
    private Long fuelTypeId;
}
