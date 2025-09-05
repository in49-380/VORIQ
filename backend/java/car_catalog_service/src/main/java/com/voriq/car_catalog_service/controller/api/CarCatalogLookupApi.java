package com.voriq.car_catalog_service.controller.api;

import com.voriq.car_catalog_service.domain.dto.CarIdDto;
import com.voriq.car_catalog_service.domain.dto.CarResolveRequest;
import com.voriq.car_catalog_service.domain.dto.IdLabelDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Car catalog lookup", description = "E")
@RequestMapping("/v1/catalog")
public interface CarCatalogLookupApi {

    @GetMapping("/brands")
    List<IdLabelDto<String>> getALlBrands();

    @GetMapping("/brands/{brandId}/models")
    List<IdLabelDto<String>> getModelsByBrand(
            @PathVariable Long brandId
    );

    @GetMapping("/brands/{brandId}/models/{modelId}/years")
    List<IdLabelDto<Integer>> getYears(
            @PathVariable
            Long brandId,
            @PathVariable
            Long modelId
    );

    @GetMapping("/brands/{brandId}/models/{modelId}/years/{yearId}/engines")
    List<IdLabelDto<String>> getEngines(
            @PathVariable Long brandId,
            @PathVariable Long modelId,
            @PathVariable Long yearId
    );

    @GetMapping("/engines/{engineId}/fuel-types")
   List<IdLabelDto<String>> getFuelTypes(
           @PathVariable Long engineId) ;

    @PostMapping("/cars/resolve")
    CarIdDto resolve(
            @Valid
            @RequestBody CarResolveRequest req) ;

}
