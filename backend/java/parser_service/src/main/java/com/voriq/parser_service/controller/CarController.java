package com.voriq.parser_service.controller;

import com.voriq.parser_service.controller.api.CarApi;
import com.voriq.parser_service.controller.base.BaseReadOnlyController;
import com.voriq.parser_service.domain.dto.CarDto;
import com.voriq.parser_service.service.CarService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CarController extends BaseReadOnlyController<CarDto, Long> implements CarApi {
    public CarController(CarService service) {
        super(service);
    }
}
