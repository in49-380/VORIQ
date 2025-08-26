package com.voriq.car_catalog_service.repository.interfaces;

import java.util.List;

public interface ModelRepository {

    List<String> findAllByBrandName(String brand);
}
