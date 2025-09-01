package com.voriq.car_catalog_service.service;

import com.voriq.car_catalog_service.domain.dto.EngineResponseDto;
import com.voriq.car_catalog_service.exception_handler.exception.ServiceUnavailableException;
import com.voriq.car_catalog_service.repository.EngineJdbcRepository;
import com.voriq.car_catalog_service.service.interfaces.ReadOnlyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EngineServiceImp implements ReadOnlyService<EngineResponseDto> {

    private final EngineJdbcRepository repository;

    @Override
    public EngineResponseDto getAll() {
        List<String> engines;
        try {
            engines = repository.findAll();
        } catch (Exception ex) {
            throw new ServiceUnavailableException(
                    "The server is currently overloaded or under maintenance. Please try again later.", ex);
        }
        return EngineResponseDto.builder()
                .items(List.copyOf(engines))
                .build();
    }
}
