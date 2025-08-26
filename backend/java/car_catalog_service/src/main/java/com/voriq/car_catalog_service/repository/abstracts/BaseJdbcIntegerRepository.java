package com.voriq.car_catalog_service.repository.abstracts;

import java.util.List;


public abstract class BaseJdbcIntegerRepository extends BaseJdbcRepository<Integer> {

    protected BaseJdbcIntegerRepository(String table, String column) {
        super(table, column);
    }

    public List<Integer> findAll() {
        String sql = String.format("""
            SELECT DISTINCT %1$s AS val
            FROM %2$s
            WHERE %1$s BETWEEN 1880 AND 2100
            ORDER BY val ASC
        """, column(), table());
        return jdbc().queryForList(sql, Integer.class);
    }
}

