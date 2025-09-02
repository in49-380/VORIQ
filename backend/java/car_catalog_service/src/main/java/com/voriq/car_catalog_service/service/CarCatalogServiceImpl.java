package com.voriq.car_catalog_service.service;

import com.voriq.car_catalog_service.domain.dto.CarIdDto;
import com.voriq.car_catalog_service.domain.dto.CarResolveRequest;
import com.voriq.car_catalog_service.domain.dto.IdValueResponseDto;
import com.voriq.car_catalog_service.service.interfaces.CarCatalogService;

import java.util.List;

public class CarCatalogServiceImpl implements CarCatalogService {
    @Override
    public List<IdValueResponseDto> findALlBrands() {
        return List.of();
    }

    @Override
    public List<IdValueResponseDto> findModelsByBrand(Long brandId) {
        return List.of();
    }

    @Override
    public List<IdValueResponseDto> findYears(Long brandId, Long modelId) {
        return List.of();
    }

    @Override
    public List<IdValueResponseDto> findEngines(Long brandId, Long modelId, Long yearId) {
        return List.of();
    }

    @Override
    public List<IdValueResponseDto> findTransmissions(Long brandId, Long modelId, Long yearId, Long engineId) {
        return List.of();
    }

    @Override
    public List<IdValueResponseDto> findDriveLayouts(Long brandId, Long modelId, Long yearId, Long engineId, Long transmissionsId) {
        return List.of();
    }

    @Override
    public CarIdDto getResolve(CarResolveRequest req) {
        return null;
    }
}
