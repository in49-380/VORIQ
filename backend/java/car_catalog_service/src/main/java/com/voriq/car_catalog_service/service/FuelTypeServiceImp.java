package com.voriq.car_catalog_service.service;

import com.voriq.car_catalog_service.domain.dto.FuelTypeResponseDto;
import com.voriq.car_catalog_service.repository.FuelTypeJdbcRepository;
import com.voriq.car_catalog_service.service.interfaces.ReadOnlyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FuelTypeServiceImp implements ReadOnlyService<FuelTypeResponseDto> {

    private final FuelTypeJdbcRepository repository;

    @Override
    public FuelTypeResponseDto getAll() {
        List<String> fuelTypes = repository.findAll();
        return FuelTypeResponseDto.builder()
                .items(List.copyOf(fuelTypes))
                .build();
    }
}
