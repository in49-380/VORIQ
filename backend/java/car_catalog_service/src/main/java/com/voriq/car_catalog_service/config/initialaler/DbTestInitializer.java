package com.voriq.car_catalog_service.config.initialaler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Profile("test")
public class DbTestInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        createSchemaIfNotExists();
        seedIfEmpty();
        System.out.println("✅ Test database initialized (H2)");
    }

    private void createSchemaIfNotExists() {

        jdbc.execute("""
                    CREATE TABLE IF NOT EXISTS brands (
                      id        BIGINT PRIMARY KEY,
                      name      VARCHAR(255) NOT NULL UNIQUE,
                      name_norm VARCHAR(255) AS (LOWER(TRIM(name)))
                    );
                """);
        jdbc.execute("CREATE INDEX IF NOT EXISTS idx_brands_name_lower ON brands (name_norm);");

        jdbc.execute("""
                    CREATE TABLE IF NOT EXISTS fuel_types (
                      id   BIGINT PRIMARY KEY,
                      name VARCHAR(64) NOT NULL UNIQUE
                    );
                """);

        jdbc.execute("""
                    CREATE TABLE IF NOT EXISTS engines (
                      id           BIGINT PRIMARY KEY,
                      type         VARCHAR(64) NOT NULL,
                      fuel_type_id BIGINT NOT NULL,
                      CONSTRAINT fk_engines_fuel FOREIGN KEY (fuel_type_id) REFERENCES fuel_types(id)
                    );
                """);
        jdbc.execute("CREATE INDEX IF NOT EXISTS idx_engines_fuel ON engines (fuel_type_id);");

        jdbc.execute("""
                    CREATE TABLE IF NOT EXISTS years (
                      id         BIGINT PRIMARY KEY,
                      year_value INTEGER NOT NULL
                    );
                """);
        jdbc.execute("CREATE INDEX IF NOT EXISTS idx_years_year ON years (year_value);");

        jdbc.execute("""
                    CREATE TABLE IF NOT EXISTS models (
                      id        BIGINT PRIMARY KEY,
                      brand_id  BIGINT NOT NULL,
                      name      VARCHAR(255) NOT NULL,
                      name_norm VARCHAR(255) AS (LOWER(TRIM(name))),
                      CONSTRAINT fk_models_brand FOREIGN KEY (brand_id) REFERENCES brands(id)
                    );
                """);
        jdbc.execute("CREATE INDEX IF NOT EXISTS idx_models_brand_id ON models (brand_id);");
        jdbc.execute("CREATE INDEX IF NOT EXISTS idx_models_name_lower ON models (name_norm);");

        jdbc.execute("""
                    CREATE TABLE IF NOT EXISTS cars (
                      id        BIGINT PRIMARY KEY,
                      model_id  BIGINT NOT NULL,
                      engine_id BIGINT NOT NULL,
                      year_id   BIGINT NOT NULL,
                      CONSTRAINT fk_cars_model  FOREIGN KEY (model_id)  REFERENCES models(id),
                      CONSTRAINT fk_cars_engine FOREIGN KEY (engine_id) REFERENCES engines(id),
                      CONSTRAINT fk_cars_year   FOREIGN KEY (year_id)   REFERENCES years(id)
                    );
                """);
        jdbc.execute("CREATE INDEX IF NOT EXISTS idx_cars_refs ON cars (model_id, engine_id, year_id);");
    }

    private void seedIfEmpty() throws Exception {
        if (count("fuel_types") == 0) {
            batchUpsertFuels(readJson("seed/fuels_db.json"));
        }
        if (count("brands") == 0) {
            batchUpsertBrands(readJson("seed/brands_db.json"));
        }
        if (count("years") == 0) {
            batchUpsertYears(readJson("seed/years_db.json"));
        }
        if (count("engines") == 0) {
            batchUpsertEngines(readJson("seed/engines_db.json"));
        }
        if (count("models") == 0) {
            batchUpsertModels(readJson("seed/models_db.json"));
        }
        if (count("cars") == 0) {
            batchUpsertCars(readJson("seed/cars_db.json"));
        }
    }

    private long count(String table) {
        Long v = jdbc.queryForObject("SELECT COUNT(*) FROM " + table, Long.class);
        return v == null ? 0 : v;
    }

    private List<Map<String, Object>> readJson(String classpath) throws Exception {
        ClassPathResource res = new ClassPathResource(classpath);
        try (InputStream is = res.getInputStream()) {
            return objectMapper.readValue(is, new TypeReference<List<Map<String, Object>>>() {
            });
        }
    }

    private void batchUpsertFuels(List<Map<String, Object>> rows) {
        jdbc.batchUpdate("""
                    MERGE INTO fuel_types (id, name) KEY(id) VALUES (?, ?)
                """, rows, 500, (ps, row) -> {
            ps.setLong(1, toLong(row.get("id")));
            ps.setString(2, toStr(row.get("name")));
        });
    }

    private void batchUpsertBrands(List<Map<String, Object>> rows) {
        jdbc.batchUpdate("""
                    MERGE INTO brands (id, name) KEY(id) VALUES (?, ?)
                """, rows, 500, (ps, row) -> {
            ps.setLong(1, toLong(row.get("id")));
            ps.setString(2, toStr(row.get("name")));
        });
    }

    private void batchUpsertYears(List<Map<String, Object>> rows) {
        jdbc.batchUpdate("""
                    MERGE INTO years (id, year_value) KEY(id) VALUES (?, ?)
                """, rows, 500, (ps, row) -> {
            ps.setLong(1, toLong(row.get("id")));
            ps.setInt(2, toInt(row.get("year")));
        });
    }

    private void batchUpsertEngines(List<Map<String, Object>> rows) {
        jdbc.batchUpdate("""
                    MERGE INTO engines (id, type, fuel_type_id) KEY(id) VALUES (?, ?, ?)
                """, rows, 500, (ps, row) -> {
            ps.setLong(1, toLong(row.get("id")));
            ps.setString(2, toStr(row.get("type")));
            ps.setLong(3, toLong(row.get("fuel_type_id")));
        });
    }

    private void batchUpsertModels(List<Map<String, Object>> rows) {
        jdbc.batchUpdate("""
                    MERGE INTO models (id, brand_id, name) KEY(id) VALUES (?, ?, ?)
                """, rows, 500, (ps, row) -> {
            ps.setLong(1, toLong(row.get("id")));
            ps.setLong(2, toLong(row.get("brand_id")));
            ps.setString(3, toStr(row.get("name")));
        });
    }

    private void batchUpsertCars(List<Map<String, Object>> rows) {
        jdbc.batchUpdate("""
                    MERGE INTO cars (id, model_id, engine_id, year_id) KEY(id) VALUES (?, ?, ?, ?)
                """, rows, 500, (ps, row) -> {
            ps.setLong(1, toLong(row.get("id")));
            ps.setLong(2, toLong(row.get("model_id")));
            ps.setLong(3, toLong(row.get("engine_id")));
            ps.setLong(4, toLong(row.get("year_id")));
        });
    }

    private static long toLong(Object v) {
        if (v == null) return 0L;
        if (v instanceof Number n) return n.longValue();
        return Long.parseLong(v.toString().trim());
    }

    private static int toInt(Object v) {
        if (v == null) return 0;
        if (v instanceof Number n) return n.intValue();
        return Integer.parseInt(v.toString().trim());
    }

    private static String toStr(Object v) {
        return v == null ? null : v.toString();
    }
}
