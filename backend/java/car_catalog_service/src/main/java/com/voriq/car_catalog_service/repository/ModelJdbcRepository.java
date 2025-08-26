package com.voriq.car_catalog_service.repository;

import com.voriq.car_catalog_service.exception_handler.exception.StatusException;
import com.voriq.car_catalog_service.repository.abstracts.BaseJdbcRepository;
import com.voriq.car_catalog_service.repository.interfaces.ModelRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ModelJdbcRepository extends BaseJdbcRepository<String> implements ModelRepository {

    private static final String TABLE = "models";
    private static final String COLUMN = "name";

    public ModelJdbcRepository() {
        super(TABLE, COLUMN);
    }

    @Override
    public List<String> findAllByBrandName(String brand) {
        String sql = String.format("""
                SELECT DISTINCT ON (lower(trim(m.%1$s))) m.%1$s
                FROM %2$s m
                JOIN brands b ON b.id = m.brand_id
                WHERE b.name IS NOT NULL
                  AND m.%1$s IS NOT NULL
                  AND length(trim(m.%1$s)) > 0
                  AND lower(trim(b.name)) = lower(trim(?))
                ORDER BY lower(trim(m.%1$s)), length(m.%1$s), m.%1$s
                """, column(), table());

        return jdbc().queryForList(sql, String.class, brand);
    }

    public List<String> findAll() {
        throw new StatusException("Method findAll for models isn't allowed.");
    }
}
