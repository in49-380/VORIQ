package com.voriq.car_catalog_service.controller;

import com.voriq.car_catalog_service.controller.api.EngineApi;
import com.voriq.car_catalog_service.controller.base.BaseReadOnlyController;
import com.voriq.car_catalog_service.domain.dto.EngineResponseDto;
import com.voriq.car_catalog_service.service.EngineServiceImp;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class EngineController extends BaseReadOnlyController<EngineResponseDto> implements EngineApi {
    public EngineController(EngineServiceImp service) {
        super(service);
    }
}
