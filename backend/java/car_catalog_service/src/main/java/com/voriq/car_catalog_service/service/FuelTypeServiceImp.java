package com.voriq.car_catalog_service.service;

import com.voriq.car_catalog_service.domain.dto.FuelTypeResponseDto;
import com.voriq.car_catalog_service.exception_handler.exception.ServiceUnavailableException;
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

        List<String> fuelTypes;
        try {
            fuelTypes = repository.findAll();
        } catch (Exception ex) {
            throw new ServiceUnavailableException(
                    "The server is currently overloaded or under maintenance. Please try again later.", ex);
        }
        return FuelTypeResponseDto.builder()
                .items(List.copyOf(fuelTypes))
                .build();
    }
}
