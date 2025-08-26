package com.voriq.car_catalog_service.repository;

import com.voriq.car_catalog_service.repository.abstracts.BaseJdbcIntegerRepository;
import org.springframework.stereotype.Repository;

@Repository
public class YearJdbcRepository extends BaseJdbcIntegerRepository {

    private static final String TABLE = "years";
    private static final String COLUMN = "year";

    public YearJdbcRepository() {
        super(TABLE,COLUMN);
    }
}

