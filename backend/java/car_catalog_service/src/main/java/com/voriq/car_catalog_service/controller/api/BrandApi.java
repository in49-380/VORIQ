package com.voriq.car_catalog_service.controller.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Brands. Deprecated. Will be disabled soon!", description = "Brand catalog endpoints")
@RequestMapping("/v1/brands")
public interface BrandApi {

}
