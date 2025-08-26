package com.voriq.car_catalog_service.service;

import com.voriq.car_catalog_service.domain.dto.CarResponseDto;
import com.voriq.car_catalog_service.domain.dto.PageCarResponseDto;
import com.voriq.car_catalog_service.repository.CarJdbcRepository;
import com.voriq.car_catalog_service.service.interfaces.CarService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarJdbcRepository repository;

    private static final Map<String, String> SORT_MAP = Map.of(
            "brand",      "b.name",
            "model",      "m.name",
            "year",       "y.year",
            "engineType", "e.name",
            "fuelType",   "f.name",
            "id",         "c.id"
    );

@Override
    public PageCarResponseDto searchCars(String brand,
                                         String model,
                                         String fuelType,
                                         String engineType,
                                         Integer yearFrom,
                                         Integer yearTo,
                                         Pageable pageable) {

            String orderBy = buildOrderBy(pageable.getSort(), "b.name");

            int limit  = pageable.getPageSize();
        int offset = (int) pageable.getOffset();

            List<CarResponseDto> content = repository.search(brand, model, fuelType, engineType, yearFrom, yearTo, orderBy, limit, offset);
        long total = repository.count(brand, model, fuelType, engineType, yearFrom, yearTo);

        Page<CarResponseDto> page = new PageImpl<>(content, pageable, total);
        return new PageCarResponseDto(page);
    }

    @Override
    public CarResponseDto getById(Long id) {
        return repository.getById(id);
    }

    private String buildOrderBy(Sort sort, String defaultColumn) {
        if (sort == null || sort.isUnsorted()) {
            return defaultColumn + " ASC, c.id ASC";
        }
        List<String> parts = new ArrayList<>();
        for (Sort.Order o : sort) {
            String apiField = o.getProperty();
            String col = SORT_MAP.get(apiField);
            if (col != null) {
                parts.add(col + " " + (o.isAscending() ? "ASC" : "DESC"));
            }
        }
        if (parts.isEmpty()) {
            parts.add(defaultColumn + " ASC");
        }
             parts.add("c.id ASC");
        return String.join(", ", parts);
    }
}

