package com.voriq.parser_service.controller.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Fuel type", description = "Fuel type catalog endpoints")
@RequestMapping("/fuel-types")
public interface FuelTypeApi {
}
