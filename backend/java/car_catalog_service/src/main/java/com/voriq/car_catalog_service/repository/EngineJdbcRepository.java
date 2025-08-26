package com.voriq.car_catalog_service.repository;

import com.voriq.car_catalog_service.repository.abstracts.BaseJdbcStringRepository;
import org.springframework.stereotype.Repository;

@Repository
public class EngineJdbcRepository extends BaseJdbcStringRepository {

    private static final String TABLE = "engines";
    private static final String COLUMN = "type";

    public EngineJdbcRepository() {
        super(TABLE,COLUMN);
    }
}

