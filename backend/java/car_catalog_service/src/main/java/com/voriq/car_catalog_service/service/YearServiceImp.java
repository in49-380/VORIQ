package com.voriq.car_catalog_service.service;

import com.voriq.car_catalog_service.domain.dto.YearResponseDto;
import com.voriq.car_catalog_service.exception_handler.exception.ServiceUnavailableException;
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
        List<Integer> years;
        try {
            years = repository.findAll();
        } catch (Exception ex) {
            throw new ServiceUnavailableException(
                    "The server is currently overloaded or under maintenance. Please try again later.", ex);
        }
        return YearResponseDto.builder()
                .items(List.copyOf(years))
                .build();
    }
}

