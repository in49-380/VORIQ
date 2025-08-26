package com.voriq.car_catalog_service.repository.abstracts;

import com.voriq.car_catalog_service.repository.interfaces.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public abstract class BaseJdbcRepository<T> implements BaseRepository<T> {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String table;
    private final String column;

    protected BaseJdbcRepository(String table, String column) {
        this.table = table;
        this.column = column;
    }

    protected String table() {
        return table;
    }

    protected String column() {
        return column;
    }

    protected JdbcTemplate jdbc() {
        return jdbcTemplate;
    }

    @Override
    public  List<T> findAll(Class<T> type) {
        String sql = String.format("""
                SELECT DISTINCT ON (lower(trim(%1$s))) %1$s
                FROM %2$s
                WHERE %1$s IS NOT NULL AND length(trim(%1$s)) > 0
                ORDER BY lower(trim(%1$s)), length(%1$s), %1$s
                """, column, table);
        return jdbc().queryForList(sql, type);
    }
}
