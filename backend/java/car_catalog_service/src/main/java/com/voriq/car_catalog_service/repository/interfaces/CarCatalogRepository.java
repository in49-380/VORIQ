package com.voriq.car_catalog_service.repository.interfaces;

import com.voriq.car_catalog_service.domain.dto.IdValueResponseDto;

import java.util.List;
import java.util.Optional;

public interface CarCatalogRepository {

    List<IdValueResponseDto> findALlBrands();

    List<IdValueResponseDto> findModelsByBrand(Long brandId);

    List<IdValueResponseDto> findYears(
            Long brandId,
            Long modelId
    );

    List<IdValueResponseDto> findEngines(
            Long brandId,
            Long modelId,
            Long yearId);

    List<IdValueResponseDto> findTransmissions(
            Long brandId,
            Long modelId,
            Long yearId,
            Long engineId);

    List<IdValueResponseDto> findDriveLayouts(
            Long brandId,
            Long modelId,
            Long yearId,
            Long engineId,
            Long transmissionsId);

    Optional<Long> getResolve(
            Long brandId,
            Long modelId,
            Long yearId,
            Long engineId,
            Long transmissionId,
            Long driveLayoutId);
}
