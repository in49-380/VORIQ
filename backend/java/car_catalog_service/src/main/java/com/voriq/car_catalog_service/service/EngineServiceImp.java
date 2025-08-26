package com.voriq.car_catalog_service.service;

import com.voriq.car_catalog_service.domain.dto.EngineResponseDto;
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
        List<String> engines = repository.findAll();
        return EngineResponseDto.builder()
                .items(List.copyOf(engines))
                .build();
    }
}
