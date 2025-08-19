package com.voriq.parser_service.controller;

import com.voriq.parser_service.controller.api.FuelTypeApi;
import com.voriq.parser_service.controller.base.BaseReadOnlyController;
import com.voriq.parser_service.domain.dto.CarDto;
import com.voriq.parser_service.domain.dto.FuelTypeDto;
import com.voriq.parser_service.service.FuelTypeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class FuelTypeController extends BaseReadOnlyController<FuelTypeDto, Long> implements FuelTypeApi {
    public FuelTypeController(FuelTypeService service) {
        super(service);
    }
}

