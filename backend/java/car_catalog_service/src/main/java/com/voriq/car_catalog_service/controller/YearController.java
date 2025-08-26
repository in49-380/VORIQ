package com.voriq.car_catalog_service.controller;

import com.voriq.car_catalog_service.controller.api.YearApi;
import com.voriq.car_catalog_service.controller.base.BaseReadOnlyController;
import com.voriq.car_catalog_service.domain.dto.YearResponseDto;
import com.voriq.car_catalog_service.service.YearServiceImp;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class YearController extends BaseReadOnlyController<YearResponseDto> implements YearApi {
    public YearController(YearServiceImp service) {
        super(service);
    }
}
