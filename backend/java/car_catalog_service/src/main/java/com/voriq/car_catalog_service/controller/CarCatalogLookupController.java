package com.voriq.car_catalog_service.controller;

import com.voriq.car_catalog_service.controller.api.CarCatalogLookupApi;
import com.voriq.car_catalog_service.domain.dto.CarIdDto;
import com.voriq.car_catalog_service.domain.dto.CarResolveRequest;
import com.voriq.car_catalog_service.domain.dto.IdValueResponseDto;
import com.voriq.car_catalog_service.service.interfaces.CarCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CarCatalogLookupController implements CarCatalogLookupApi {

    private CarCatalogService service;

    @Override
    public ResponseEntity<List<IdValueResponseDto>> getALlBrands() {
        return ResponseEntity.ok(service.findALlBrands());
    }

    @Override
    public ResponseEntity<List<IdValueResponseDto>> getModelsByBrand(Long brandId) {
        return ResponseEntity.ok(service.findModelsByBrand(brandId));
    }

    @Override
    public ResponseEntity<List<IdValueResponseDto>> getYears(Long brandId, Long modelId) {
        return ResponseEntity.ok(service.findYears(brandId, modelId));
    }

    @Override
    public ResponseEntity<List<IdValueResponseDto>> getEngines(Long brandId, Long modelId, Long yearId) {
        return ResponseEntity.ok(service.findEngines(brandId, modelId, yearId));
    }

    @Override
    public ResponseEntity<List<IdValueResponseDto>> getTransmissions(Long brandId, Long modelId, Long yearId, Long engineId) {
        return ResponseEntity.ok(service.findTransmissions(brandId, modelId, yearId, engineId));
    }

    @Override
    public ResponseEntity<List<IdValueResponseDto>> getDriveLayouts(Long brandId, Long modelId, Long yearId, Long engineId, Long transmissionsId) {
        return ResponseEntity.ok(service.findDriveLayouts(brandId, modelId, yearId, engineId, transmissionsId));
    }

    @Override
    public ResponseEntity<CarIdDto> resolve(CarResolveRequest req) {
        return ResponseEntity.ok(service.getResolve(req));
    }
}
