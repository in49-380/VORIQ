package com.voriq.car_catalog_service.controller.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Fuel type", description = "Fuel type catalog endpoints")
@RequestMapping("/v1/fuel-types")
public interface FuelTypeApi {
}
