package com.voriq.parser_service.controller;

import com.voriq.parser_service.controller.api.EngineApi;
import com.voriq.parser_service.controller.api.FuelTypeApi;
import com.voriq.parser_service.domain.dto.FuelTypeDto;
import com.voriq.parser_service.service.interfaces.EngineService;
import com.voriq.parser_service.service.interfaces.FuelTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/fuel-types")
@RequiredArgsConstructor
public class FuelTypeController  implements FuelTypeApi {

    private final FuelTypeService fuelTypeService;

    @Override
    @GetMapping
    public ResponseEntity<List<FuelTypeDto>> getAllFuelTypes() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(fuelTypeService.getAllFuelTypes());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<FuelTypeDto> getFuelTypeById(Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(fuelTypeService.getFuelTypeById(id));
    }
}
