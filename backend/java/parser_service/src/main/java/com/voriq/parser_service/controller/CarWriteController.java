package com.voriq.parser_service.controller;

import com.voriq.parser_service.controller.annotation.PostCarSwaggerApi;
import com.voriq.parser_service.domain.dto.request.CarRequestDto;
import com.voriq.parser_service.service.interfaces.CarWriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarWriteController {

    private final CarWriteService carWriteService;

    @Operation(
            summary = "Add new cars",
            description = "Add new cars"
    )
    @PostCarSwaggerApi
    @PostMapping
    public ResponseEntity<Void> createCars(@RequestBody List<CarRequestDto> cars) {
        carWriteService.saveCars(cars);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
