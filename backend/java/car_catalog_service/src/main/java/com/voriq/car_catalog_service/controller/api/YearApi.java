package com.voriq.car_catalog_service.controller.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Year. Deprecated. Will be disabled soon!", description = "Year catalog endpoints")
@RequestMapping("/v1/years")
public interface YearApi {
}
