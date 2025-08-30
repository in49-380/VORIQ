package com.voriq.car_catalog_service.controller.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Engine", description = "Engine catalog endpoints")
@RequestMapping("/v1/engines")
public interface EngineApi {
}
