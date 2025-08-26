package com.voriq.car_catalog_service.repository.abstracts;

import java.util.List;


public abstract class BaseJdbcStringRepository extends BaseJdbcRepository<String> {

    protected BaseJdbcStringRepository(String table, String column) {
        super(table, column);
    }

    public List<String> findAll() {
        return findAll(String.class);
    }
}
