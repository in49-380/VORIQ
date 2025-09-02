package com.voriq.car_catalog_service.repository;

import com.voriq.car_catalog_service.domain.dto.CarResponseDto;
import com.voriq.car_catalog_service.repository.interfaces.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CarJdbcRepository implements CarRepository {

    private final JdbcTemplate jdbc;

    private static final RowMapper<CarResponseDto> CAR_ROW = (rs, n) ->
            CarResponseDto.builder()
                    .id(rs.getLong("id"))
                    .model(rs.getString("model"))
                    .engine(rs.getString("engine"))
                    .fuelType(rs.getString("fuel_type"))
                    .brand(rs.getString("brand"))
                    .year(rs.getInt("year"))
                    .build();

    private static final String BASE_FROM = """
    FROM cars c
    JOIN models      m ON m.id = c.model_id
    JOIN brands      b ON b.id = m.brand_id
    JOIN engines     e ON e.id = c.engine_id
    JOIN fuel_types  f ON f.id = e.fuel_type_id
    JOIN years       y ON y.id = c.year_id
    """;

    private static final String BASE_SELECT = """
    SELECT  c.id,
            m.name       AS model,
            e.type       AS engine,
            f.name       AS fuel_type,
            b.name       AS brand,
            y.year_value AS year
    """ + BASE_FROM;
@Override
    public List<CarResponseDto> search(String brand, String model, String fuelType, String engineType,
                                       Integer yearFrom, Integer yearTo,
                                       String orderBy, int limit, int offset) {

        SqlAndArgs wa = buildWhere(brand, model, fuelType, engineType, yearFrom, yearTo);
        String sql = BASE_SELECT + wa.sql
                + " ORDER BY " + orderBy
                + " LIMIT ? OFFSET ?";

        List<Object> args = new ArrayList<>(wa.args);
        args.add(limit);
        args.add(offset);

        return jdbc.query(sql, CAR_ROW, args.toArray());
    }

    @Override
    public CarResponseDto getById(Long id) {
        String sql = BASE_SELECT + " WHERE c.id = ?";

        return jdbc.queryForObject(sql, CAR_ROW, id);
    }

    @Override
    public long count(String brand, String model, String fuelType, String engineType,
                      Integer yearFrom, Integer yearTo) {
        SqlAndArgs wa = buildWhere(brand, model, fuelType, engineType, yearFrom, yearTo);
        String sql = "SELECT COUNT(*) " + BASE_FROM + wa.sql;
        return jdbc.queryForObject(sql, wa.args.toArray(), Long.class);
    }

    private SqlAndArgs buildWhere(String brand, String model, String fuelType, String engineType,
                                  Integer yearFrom, Integer yearTo) {
        StringBuilder where = new StringBuilder(" WHERE 1=1 ");
        List<Object> args = new ArrayList<>();

        if (brand != null && !brand.isBlank()) {
            where.append(" AND lower(trim(b.name)) = lower(trim(?)) ");
            args.add(brand);
        }
        if (model != null && !model.isBlank()) {
            where.append(" AND lower(trim(m.name)) = lower(trim(?)) ");
            args.add(model);
        }
        if (fuelType != null && !fuelType.isBlank()) {
            where.append(" AND lower(trim(f.name)) = lower(trim(?)) ");
            args.add(fuelType);
        }
        if (engineType != null && !engineType.isBlank()) {
            where.append(" AND lower(trim(e.type)) = lower(trim(?)) ");
            args.add(engineType);
        }
        if (yearFrom != null) {
            where.append(" AND y.year_value >= ? ");
            args.add(yearFrom);
        }
        if (yearTo != null) {
            where.append(" AND y.year_value <= ? ");
            args.add(yearTo);
        }

        SqlAndArgs res = new SqlAndArgs();
        res.sql = where.toString();
        res.args = args;
        return res;
    }

    private static class SqlAndArgs {
        String sql;
        List<Object> args;
    }
}
