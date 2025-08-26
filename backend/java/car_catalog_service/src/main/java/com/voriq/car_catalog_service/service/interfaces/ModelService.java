package com.voriq.car_catalog_service.service.interfaces;

import com.voriq.car_catalog_service.domain.dto.ModelResponseDto;

public interface ModelService {

    ModelResponseDto getAllByBrand(String brand);
}
