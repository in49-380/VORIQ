package com.voriq.car_catalog_service.repository.interfaces;

import com.voriq.car_catalog_service.domain.dto.CarResponseDto;

import java.util.List;

public interface CarRepository  {

    List<CarResponseDto> search(String brand, String model, String fuelType, String engineType,
                                Integer yearFrom, Integer yearTo,
                                String orderBy, int limit, int offset);

    CarResponseDto getById(Long id);

    long count(String brand, String model, String fuelType, String engineType,
               Integer yearFrom, Integer yearTo);
}
