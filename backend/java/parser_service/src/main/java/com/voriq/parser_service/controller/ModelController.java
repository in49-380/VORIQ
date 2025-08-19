package com.voriq.parser_service.controller;

import com.voriq.parser_service.controller.api.ModelApi;
import com.voriq.parser_service.controller.base.BaseReadOnlyController;
import com.voriq.parser_service.domain.dto.ModelDto;
import com.voriq.parser_service.service.ModelService;
import org.springframework.web.bind.annotation.RestController;


@RestController

public class ModelController extends BaseReadOnlyController<ModelDto, Long> implements ModelApi {
    public ModelController(ModelService service) {
        super(service);
    }
}
