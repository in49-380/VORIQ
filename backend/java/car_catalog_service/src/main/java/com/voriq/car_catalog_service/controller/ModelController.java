package com.voriq.car_catalog_service.controller;

import com.voriq.car_catalog_service.controller.api.ModelApi;
import com.voriq.car_catalog_service.domain.dto.ModelResponseDto;
import com.voriq.car_catalog_service.service.interfaces.ModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Profile("dev")
public class ModelController implements ModelApi {

    private final ModelService service;

    @Override
    public ResponseEntity<ModelResponseDto> getAllByBrand(String brand) {
        return ResponseEntity.ok(service.getAllByBrand(brand));
    }
}
