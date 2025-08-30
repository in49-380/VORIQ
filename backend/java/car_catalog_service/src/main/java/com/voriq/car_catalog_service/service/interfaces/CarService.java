package com.voriq.car_catalog_service.service.interfaces;

import com.voriq.car_catalog_service.domain.dto.CarResponseDto;
import com.voriq.car_catalog_service.domain.dto.PageCarResponseDto;
import org.springframework.data.domain.Pageable;

public interface CarService {

    PageCarResponseDto searchCars(String brand,
                                  String model,
                                  String fuelType,
                                  String engineType,
                                  Integer yearFrom,
                                  Integer yearTo,
                                  Pageable pageable);

    CarResponseDto getById(Long id);
}
