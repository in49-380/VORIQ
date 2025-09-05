package com.voriq.car_catalog_service.controller;

import com.voriq.car_catalog_service.controller.api.CarApi;
import com.voriq.car_catalog_service.domain.dto.CarResponseDto;
import com.voriq.car_catalog_service.domain.dto.PageCarResponseDto;
import com.voriq.car_catalog_service.service.interfaces.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static com.voriq.car_catalog_service.utilitie.PaginationUtilities.getPageable;

@RestController
@RequiredArgsConstructor
@Profile("dev")
public class CarController implements CarApi {

    private final CarService service;

    @Override
    public ResponseEntity<PageCarResponseDto> searchCars(String brand,
                                                         String model,
                                                         String fuelType,
                                                         String engineType,
                                                         Integer yearFrom,
                                                         Integer yearTo,
                                                         int page,
                                                         int size,
                                                         String sortBy,
                                                         Boolean isAsc) {
        Pageable pageable = getPageable(page, size, sortBy, isAsc);

        return ResponseEntity.ok(service.searchCars(brand,
                model,
                fuelType,
                engineType,
                yearFrom,
                yearTo,
                pageable));
    }

    @Override
    public ResponseEntity<CarResponseDto> getById(Long id) {

        return ResponseEntity.ok(service.getById(id));
    }
}
