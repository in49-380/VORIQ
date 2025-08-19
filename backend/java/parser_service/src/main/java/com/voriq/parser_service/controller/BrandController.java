package com.voriq.parser_service.controller;

import com.voriq.parser_service.controller.api.BrandApi;
import com.voriq.parser_service.controller.base.BaseReadOnlyController;
import com.voriq.parser_service.domain.dto.BrandDto;
import com.voriq.parser_service.service.BrandService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BrandController extends BaseReadOnlyController<BrandDto, Long> implements BrandApi {
    public BrandController(BrandService service) {
        super(service);
    }
}

