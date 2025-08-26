package com.voriq.car_catalog_service.controller;

import com.voriq.car_catalog_service.controller.api.BrandApi;
import com.voriq.car_catalog_service.controller.base.BaseReadOnlyController;
import com.voriq.car_catalog_service.domain.dto.BrandResponseDto;
import com.voriq.car_catalog_service.service.BrandServiceImp;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BrandController extends BaseReadOnlyController<BrandResponseDto> implements BrandApi {
    public BrandController(BrandServiceImp service) {
        super(service);
    }
}

