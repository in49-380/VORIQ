package com.voriq.car_catalog_service.controller;

import com.voriq.car_catalog_service.controller.api.FuelTypeApi;
import com.voriq.car_catalog_service.controller.base.BaseReadOnlyController;
import com.voriq.car_catalog_service.domain.dto.FuelTypeResponseDto;
import com.voriq.car_catalog_service.service.FuelTypeServiceImp;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class FuelTypeController extends BaseReadOnlyController<FuelTypeResponseDto> implements FuelTypeApi {
    public FuelTypeController(FuelTypeServiceImp service) {
        super(service);
    }
}

