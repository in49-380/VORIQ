package com.voriq.parser_service.controller;

import com.voriq.parser_service.controller.api.EngineApi;
import com.voriq.parser_service.controller.base.BaseReadOnlyController;
import com.voriq.parser_service.domain.dto.EngineDto;
import com.voriq.parser_service.service.EngineService;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class EngineController extends BaseReadOnlyController<EngineDto, Long> implements EngineApi {
    public EngineController(EngineService service) {
        super(service);
    }
}
