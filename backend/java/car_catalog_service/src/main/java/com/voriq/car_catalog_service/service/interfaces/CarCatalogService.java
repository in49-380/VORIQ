package com.voriq.car_catalog_service.service.interfaces;

import com.voriq.car_catalog_service.domain.dto.CarIdDto;
import com.voriq.car_catalog_service.domain.dto.CarResolveRequest;
import com.voriq.car_catalog_service.domain.dto.IdValueResponseDto;

import java.util.List;

public interface CarCatalogService {

    List<IdValueResponseDto> findALlBrands();

    List<IdValueResponseDto> findModelsByBrand(Long brandId);

    List<IdValueResponseDto> findYears(Long brandId, Long modelId);

    List<IdValueResponseDto> findEngines(Long brandId, Long modelId, Long yearId);

    List<IdValueResponseDto> findTransmissions(Long brandId, Long modelId, Long yearId, Long engineId);

    List<IdValueResponseDto> findDriveLayouts(Long brandId, Long modelId, Long yearId,
                                              Long engineId, Long transmissionsId);

    CarIdDto getResolve(CarResolveRequest req);
}
