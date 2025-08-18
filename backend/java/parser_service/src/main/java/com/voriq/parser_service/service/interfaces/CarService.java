package com.voriq.parser_service.service.interfaces;

import com.voriq.parser_service.domain.dto.CarDto;
import com.voriq.parser_service.domain.entity.Car;

import java.util.List;

public interface CarService {
    List<CarDto> getAllCars ();
     CarDto getCarById (Long id);
}
