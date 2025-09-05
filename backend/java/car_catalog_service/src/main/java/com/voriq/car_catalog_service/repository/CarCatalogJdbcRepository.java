package com.voriq.car_catalog_service.repository;

import com.voriq.car_catalog_service.domain.dto.IdValueResponseDto;
import com.voriq.car_catalog_service.repository.interfaces.CarCatalogRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CarCatalogJdbcRepository implements CarCatalogRepository {


    private final JdbcTemplate jdbc;

    public CarCatalogJdbcRepository(@Qualifier("voriqJdbcTemplate")
                                    JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<IdValueResponseDto> ID_VALUE_ROW =
            (rs, n) -> new IdValueResponseDto(rs.getLong("id"), rs.getString("value"));

    @Override
    public List<IdValueResponseDto> findALlBrands() {
        final String sql = """
            SELECT DISTINCT b.id, b.name AS value
            FROM cars_brand b
            WHERE b.name IS NOT NULL AND length(trim(b.name)) > 0
            ORDER BY 2
            """;
        return jdbc.query(sql, ID_VALUE_ROW);
    }

    @Override
    public List<IdValueResponseDto> findModelsByBrand(Long brandId) {
        final String sql = """
            SELECT DISTINCT m.id, m.name AS value
            FROM cars_carmodel m
            WHERE m.brand_id = ?
              AND m.name IS NOT NULL AND length(trim(m.name)) > 0
            ORDER BY 2
            """;
        return jdbc.query(sql, ID_VALUE_ROW, brandId);
    }

    @Override
    public List<IdValueResponseDto> findYears(Long brandId, Long modelId) {
        final String sql = """
        SELECT DISTINCT y.id, y.year AS value
        FROM cars_car c
        JOIN cars_year y ON y.id = c.year_id
        WHERE c.brand_id = ? AND c.model_id = ?
        ORDER BY y.year
        """;

        return jdbc.query(sql, ID_VALUE_ROW, brandId, modelId);
    }

    @Override
    public List<IdValueResponseDto> findEngines(Long brandId, Long modelId, Long yearId) {
        final String sql = """
        SELECT DISTINCT e.id, e.type AS value
        FROM cars_car c
        JOIN cars_engine e ON e.id = c.engine_id
        WHERE c.brand_id = ? AND c.model_id = ? AND c.year_id = ?
        ORDER BY e.type
        """;

        return jdbc.query(sql, ID_VALUE_ROW, brandId, modelId, yearId);
    }


    @Override
    public List<IdValueResponseDto> findTransmissions(Long brandId, Long modelId, Long yearId, Long engineId) {
        // label = Manual/Automatic по флагу manual
        final String sql = """
            SELECT DISTINCT t.id,
                   CASE WHEN t.manual THEN 'Manual' ELSE 'Automatic' END AS value
            FROM cars_car c
            JOIN cars_transmission t ON t.id = c.transmission_id
            WHERE c.brand_id = ? AND c.model_id = ? AND c.year_id = ? AND c.engine_id = ?
            ORDER BY 2
            """;
        return jdbc.query(sql, ID_VALUE_ROW, brandId, modelId, yearId, engineId);
    }

    @Override
    public List<IdValueResponseDto> findWheelDrives(Long brandId, Long modelId, Long yearId, Long engineId, Long transmissionsId) {
        final String sql = """
            SELECT DISTINCT w.id, w.name AS value
            FROM cars_car c
            JOIN cars_whilldrive w ON w.id = c.wheel_drive_id
            WHERE c.brand_id = ? AND c.model_id = ? AND c.year_id = ? AND c.engine_id = ? AND c.transmission_id = ?
            ORDER BY 2
            """;
        return jdbc.query(sql, ID_VALUE_ROW, brandId, modelId, yearId, engineId, transmissionsId);
    }

    @Override
    public Optional<Long> getResolve(Long brandId,
                                     Long modelId,
                                     Long yearId,
                                     Long engineId,
                                     Long transmissionId,
                                     Long driveLayoutId) {
        final String sql = """
                SELECT c.id
                FROM cars_car c
                WHERE c.brand_id = ?
                  AND c.model_id = ?
                  AND c.year_id = ?
                  AND c.engine_id = ?
                  AND c.transmission_id = ?
                  AND c.wheel_drive_id = ?
                LIMIT 1
                """;
        List<Long> ids = jdbc.query(sql,
                (rs, n) -> rs.getLong(1),
                brandId, modelId, yearId, engineId, transmissionId, driveLayoutId);

        return ids.isEmpty() ? Optional.empty() : Optional.of(ids.get(0));
    }
}

