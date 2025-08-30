package com.voriq.car_catalog_service.repository;

import com.voriq.car_catalog_service.repository.abstracts.BaseJdbcStringRepository;
import org.springframework.stereotype.Repository;

@Repository
public class BrandJdbcRepository extends BaseJdbcStringRepository {

    private static final String TABLE = "brands";
    private static final String COLUMN = "name";

    public BrandJdbcRepository() {
        super(TABLE,COLUMN);
    }
}

