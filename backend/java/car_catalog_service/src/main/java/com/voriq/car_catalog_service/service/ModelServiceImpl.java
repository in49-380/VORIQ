package com.voriq.car_catalog_service.service;

import com.voriq.car_catalog_service.domain.dto.ModelResponseDto;
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
        List<String> models = repository.findAllByBrandName(brand);
        return ModelResponseDto.builder()
                .items(List.copyOf(models))
                .build();
    }
}
