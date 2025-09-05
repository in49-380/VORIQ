package com.voriq.car_catalog_service.controller;

import com.voriq.car_catalog_service.controller.api.CarCatalogLookupApi;
import com.voriq.car_catalog_service.domain.dto.CarIdDto;
import com.voriq.car_catalog_service.domain.dto.CarResolveRequest;
import com.voriq.car_catalog_service.domain.dto.IdLabelDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CarCatalogLookupController implements CarCatalogLookupApi {
    @Override
    public List<IdLabelDto<String>> getALlBrands() {
        return List.of();
    }

    @Override
    public List<IdLabelDto<String>> getModelsByBrand(Long brandId) {
        return List.of();
    }

    @Override
    public List<IdLabelDto<Integer>> getYears(Long brandId, Long modelId) {
        return List.of();
    }

    @Override
    public List<IdLabelDto<String>> getEngines(Long brandId, Long modelId, Long yearId) {
        return List.of();
    }

    @Override
    public List<IdLabelDto<String>> getFuelTypes(Long engineId) {
        return List.of();
    }

    @Override
    public CarIdDto resolve(CarResolveRequest req) {
        return null;
    }
}
