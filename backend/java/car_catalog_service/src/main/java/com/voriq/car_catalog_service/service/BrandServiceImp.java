package com.voriq.car_catalog_service.service;

import com.voriq.car_catalog_service.domain.dto.BrandResponseDto;
import com.voriq.car_catalog_service.exception_handler.exception.ServiceUnavailableException;
import com.voriq.car_catalog_service.repository.BrandJdbcRepository;
import com.voriq.car_catalog_service.service.interfaces.ReadOnlyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceImp implements ReadOnlyService<BrandResponseDto> {

    private final BrandJdbcRepository repository;

    @Override
    public BrandResponseDto getAll() {
        List<String> brands;
        try {
            brands = repository.findAll();
        } catch (Exception ex) {
            throw new ServiceUnavailableException(
                    "The server is currently overloaded or under maintenance. Please try again later.", ex);
        }
        return BrandResponseDto.builder()
                .items(List.copyOf(brands))
                .build();
    }
}


