package com.voriq.parser_service.controller;

import com.voriq.parser_service.controller.api.CarApi;
import com.voriq.parser_service.controller.api.ModelApi;
import com.voriq.parser_service.domain.dto.CarDto;
import com.voriq.parser_service.domain.entity.Car;
import com.voriq.parser_service.service.interfaces.CarService;
import com.voriq.parser_service.service.interfaces.ModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController implements CarApi {

    private final CarService carService;

    @Override
    @GetMapping
    public ResponseEntity<List<CarDto>> getAllCars() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(carService.getAllCars());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<CarDto> getCarById(Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(carService.getCarById(id));
    }
}
