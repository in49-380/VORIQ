package com.voriq.parser_service.controller.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Engine", description = "Engine catalog endpoints")
@RequestMapping("/engines")
public interface EngineApi {
}
