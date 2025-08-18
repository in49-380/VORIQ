package com.voriq.parser_service.controller.api;

import com.voriq.parser_service.domain.dto.CarDto;
import com.voriq.parser_service.domain.entity.Car;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

public interface CarApi {
    ResponseEntity<List<CarDto>> getAllCars();
    ResponseEntity<CarDto> getCarById(@PathVariable Long id);
}
