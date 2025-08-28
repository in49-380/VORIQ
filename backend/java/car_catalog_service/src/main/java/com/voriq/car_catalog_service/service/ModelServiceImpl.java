package com.voriq.car_catalog_service.service;

import com.voriq.car_catalog_service.domain.dto.ModelResponseDto;
import com.voriq.car_catalog_service.exception_handler.exception.ServiceUnavailableException;
import com.voriq.car_catalog_service.repository.ModelJdbcRepository;
import com.voriq.car_catalog_service.service.interfaces.ModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

    private final ModelJdbcRepository repository;

    @Override
    public ModelResponseDto getAllByBrand(String brand) {
        List<String> models;
        try {
            models = repository.findAllByBrandName(brand);
        } catch (Exception ex) {
            throw new ServiceUnavailableException(
                    "The server is currently overloaded or under maintenance. Please try again later.", ex);
        }
        return ModelResponseDto.builder()
                .items(List.copyOf(models))
                .build();
    }
}
