package com.voriq.car_catalog_service.config.initialaler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
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
@Profile("dev")
public class DbInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DbInitializer(@Qualifier("carsJdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        createSchemaIfNotExists();
        seedIfEmpty();
    }

    private void createSchemaIfNotExists() {
        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS brands (
              id   BIGINT PRIMARY KEY,
              name VARCHAR(255) NOT NULL UNIQUE
            );
        """);

        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS fuel_types (
              id   BIGINT PRIMARY KEY,
              name VARCHAR(64) NOT NULL UNIQUE
            );
        """);

        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS engines (
              id            BIGINT PRIMARY KEY,
              type          VARCHAR(64) NOT NULL,
              fuel_type_id  BIGINT NOT NULL REFERENCES fuel_types(id)
            );
        """);

        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS years (
              id         BIGINT PRIMARY KEY,
              year_value INTEGER NOT NULL
            );
        """);

        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS models (
              id        BIGINT PRIMARY KEY,
              brand_id  BIGINT NOT NULL REFERENCES brands(id),
              name      VARCHAR(255) NOT NULL
            );
        """);

        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS cars (
              id          BIGINT PRIMARY KEY,
              model_id    BIGINT NOT NULL REFERENCES models(id),
              engine_id   BIGINT NOT NULL REFERENCES engines(id),
              year_id     BIGINT NOT NULL REFERENCES years(id)
            );
        """);

        jdbc.execute("CREATE INDEX IF NOT EXISTS idx_brands_name_lower ON brands ((lower(trim(name))));");
        jdbc.execute("CREATE INDEX IF NOT EXISTS idx_models_brand_id ON models (brand_id);");
        jdbc.execute("CREATE INDEX IF NOT EXISTS idx_models_name_lower ON models ((lower(trim(name))));");
        jdbc.execute("CREATE INDEX IF NOT EXISTS idx_engines_fuel ON engines (fuel_type_id);");
        jdbc.execute("CREATE INDEX IF NOT EXISTS idx_years_year ON years (year_value);");
        jdbc.execute("CREATE INDEX IF NOT EXISTS idx_cars_refs ON cars (model_id, engine_id, year_id);");
    }

    private void seedIfEmpty() throws Exception {
        if (count("fuel_types") == 0) {
            List<Map<String, Object>> fuels = readJson("seed/fuels_db.json");
            batchUpsertFuels(fuels);
        }
        if (count("brands") == 0) {
            List<Map<String, Object>> brands = readJson("seed/brands_db.json");
            batchUpsertBrands(brands);
        }
        if (count("years") == 0) {
            List<Map<String, Object>> years = readJson("seed/years_db.json");
            batchUpsertYears(years);
        }
        if (count("engines") == 0) {
            List<Map<String, Object>> engines = readJson("seed/engines_db.json");
            batchUpsertEngines(engines);
        }
        if (count("models") == 0) {
            List<Map<String, Object>> models = readJson("seed/models_db.json");
            batchUpsertModels(models);
        }
        if (count("cars") == 0) {
            List<Map<String, Object>> cars = readJson("seed/cars_db.json");
            batchUpsertCars(cars);
        }

        System.out.println("✅ Dev database initialized");
    }

    private long count(String table) {
        Long v = jdbc.queryForObject("SELECT COUNT(*) FROM " + table, Long.class);
        return v == null ? 0 : v;
    }

    private List<Map<String, Object>> readJson(String classpath) throws Exception {
        ClassPathResource res = new ClassPathResource(classpath);
        try (InputStream is = res.getInputStream()) {
            return objectMapper.readValue(is, new TypeReference<List<Map<String, Object>>>() {});
        }
    }

    private void batchUpsertFuels(List<Map<String, Object>> rows) {
        jdbc.batchUpdate("""
            INSERT INTO fuel_types (id, name)
            VALUES (?, ?)
            ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name
        """, rows, 500, (ps, row) -> {
            ps.setLong(1, toLong(row.get("id")));
            ps.setString(2, toStr(row.get("name")));
        });
    }

    private void batchUpsertBrands(List<Map<String, Object>> rows) {
        jdbc.batchUpdate("""
            INSERT INTO brands (id, name)
            VALUES (?, ?)
            ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name
        """, rows, 500, (ps, row) -> {
            ps.setLong(1, toLong(row.get("id")));
            ps.setString(2, toStr(row.get("name")));
        });
    }

    private void batchUpsertYears(List<Map<String, Object>> rows) {
        jdbc.batchUpdate("""
            INSERT INTO years (id, year_value)
            VALUES (?, CAST(? AS INT))
            ON CONFLICT (id) DO UPDATE SET year_value = EXCLUDED.year_value
        """, rows, 500, (ps, row) -> {
            ps.setLong(1, toLong(row.get("id")));
            ps.setString(2, toStr(row.get("year"))); // из JSON "year" -> в БД year_value
        });
    }

    private void batchUpsertEngines(List<Map<String, Object>> rows) {
        jdbc.batchUpdate("""
            INSERT INTO engines (id, type, fuel_type_id)
            VALUES (?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET type = EXCLUDED.type, fuel_type_id = EXCLUDED.fuel_type_id
        """, rows, 500, (ps, row) -> {
            ps.setLong(1, toLong(row.get("id")));
            ps.setString(2, toStr(row.get("type")));
            ps.setLong(3, toLong(row.get("fuel_type_id")));
        });
    }

    private void batchUpsertModels(List<Map<String, Object>> rows) {
        jdbc.batchUpdate("""
            INSERT INTO models (id, brand_id, name)
            VALUES (?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET brand_id = EXCLUDED.brand_id, name = EXCLUDED.name
        """, rows, 500, (ps, row) -> {
            ps.setLong(1, toLong(row.get("id")));
            ps.setLong(2, toLong(row.get("brand_id")));
            ps.setString(3, toStr(row.get("name")));
        });
    }

    private void batchUpsertCars(List<Map<String, Object>> rows) {
        jdbc.batchUpdate("""
            INSERT INTO cars (id, model_id, engine_id, year_id)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET model_id = EXCLUDED.model_id, engine_id = EXCLUDED.engine_id, year_id = EXCLUDED.year_id
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

    private static String toStr(Object v) {
        return v == null ? null : v.toString();
    }
}
