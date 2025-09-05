package com.voriq.parser_service.controller.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Model", description = "Model catalog endpoints")
@RequestMapping("/models")
public interface ModelApi {
}
