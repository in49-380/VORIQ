package com.voriq.car_catalog_service.repository;

import com.voriq.car_catalog_service.repository.abstracts.BaseJdbcStringRepository;
import org.springframework.stereotype.Repository;

@Repository
public class FuelTypeJdbcRepository extends BaseJdbcStringRepository {

    private static final String TABLE = "fuel_types";
    private static final String COLUMN = "name";

    public FuelTypeJdbcRepository() {
        super(TABLE,COLUMN);
    }
}

