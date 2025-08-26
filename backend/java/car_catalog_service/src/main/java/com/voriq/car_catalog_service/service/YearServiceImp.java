package com.voriq.car_catalog_service.service;

import com.voriq.car_catalog_service.domain.dto.YearResponseDto;
import com.voriq.car_catalog_service.repository.YearJdbcRepository;
import com.voriq.car_catalog_service.service.interfaces.ReadOnlyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class YearServiceImp implements ReadOnlyService<YearResponseDto> {

    private final YearJdbcRepository repository;

    @Override
    public YearResponseDto getAll() {
        List<Integer> years = repository.findAll();
        return YearResponseDto.builder()
                .items(List.copyOf(years))
                .build();
    }
}

