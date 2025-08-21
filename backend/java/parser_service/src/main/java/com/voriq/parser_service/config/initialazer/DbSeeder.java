package com.voriq.parser_service.config.initialazer;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;

@Component
@RequiredArgsConstructor
@Profile("dev")
public class DbSeeder implements ApplicationRunner {
    private final JdbcTemplate jdbc;
    private final ObjectMapper om = new ObjectMapper();

    @Value("classpath:seed/fuels_db.json")   Resource fuelsRes;
    @Value("classpath:seed/brands_db.json")  Resource brandsRes;
    @Value("classpath:seed/years_db.json")   Resource yearsRes;
    @Value("classpath:seed/engines_db.json") Resource enginesRes;
    @Value("classpath:seed/models_db.json")  Resource modelsRes;

    @Override
    @Transactional
    public void run(org.springframework.boot.ApplicationArguments args) throws Exception {

        Map<Integer, Long> fuelIdMap  = upsertFuels(readList(fuelsRes));
        Map<Integer, Long> brandIdMap = upsertBrands(readList(brandsRes));
        upsertYears(readList(yearsRes));
        upsertEngines(readList(enginesRes), fuelIdMap);
        upsertModels(readList(modelsRes), brandIdMap);
    }

    private List<Map<String, Object>> readList(Resource res) throws Exception {
        try (InputStream in = res.getInputStream()) {
            return om.readValue(in, new TypeReference<>() {});
        }
    }

    private Map<Integer, Long> upsertFuels(List<Map<String, Object>> list) {
        Map<Integer, Long> map = new HashMap<>();
        String sql = """
            INSERT INTO fuel_types(name)
            VALUES (?)
            ON CONFLICT (name) DO UPDATE SET name = EXCLUDED.name
            RETURNING id
            """;
        for (Map<String, Object> row : list) {
            Integer oldId = toInt(row.get("id"));                 // только для карты
            String  name  = String.valueOf(row.get("name")).trim();

            Long dbId = jdbc.queryForObject(sql, Long.class, name);
            if (oldId != null && dbId != null) map.put(oldId, dbId);
        }
        return map;
    }

    private Map<Integer, Long> upsertBrands(List<Map<String, Object>> list) {
        Map<Integer, Long> map = new HashMap<>();
        String sql = """
            INSERT INTO brands(name)
            VALUES (?)
            ON CONFLICT (name) DO UPDATE SET name = EXCLUDED.name
            RETURNING id
            """;
        for (Map<String, Object> row : list) {
            Integer oldId = toInt(row.get("id"));                 // только для карты
            String  name  = String.valueOf(row.get("name")).trim();

            Long dbId = jdbc.queryForObject(sql, Long.class, name);
            if (oldId != null && dbId != null) map.put(oldId, dbId);
        }
        return map;
    }

    private void upsertYears(List<Map<String, Object>> list) {
        String sql = """
            INSERT INTO years(year)
            VALUES (?)
            ON CONFLICT (year) DO UPDATE SET year = EXCLUDED.year
            """;
        jdbc.batchUpdate(sql, list, 500, (ps, row) -> {
            // JSON может хранить как строку "2023", так и число 2023
            Integer year = parseYear(row.get("year"));
            ps.setInt(1, Objects.requireNonNull(year, "year is null"));
        });
    }

    private void upsertEngines(List<Map<String, Object>> list, Map<Integer, Long> fuelIdMap) {
        String sql = """
            INSERT INTO engines(type, fuel_type_id)
            VALUES (?, ?)
            ON CONFLICT (type, fuel_type_id) DO UPDATE
                SET type = EXCLUDED.type
            RETURNING id
            """;
        for (Map<String, Object> row : list) {
            String  type        = String.valueOf(row.get("type")).trim();
            Integer jsonFuelId  = toInt(row.get("fuel_type_id")); // из JSON
            Long    dbFuelId    = fuelIdMap.get(jsonFuelId);
            if (dbFuelId == null) {
                throw new IllegalStateException("No fuel mapping for json fuel_type_id=" + jsonFuelId);
            }
            jdbc.queryForObject(sql, Long.class, type, dbFuelId);
        }
    }

    private void upsertModels(List<Map<String, Object>> list, Map<Integer, Long> brandIdMap) {
        String sql = """
            INSERT INTO models(brand_id, name)
            VALUES (?, ?)
            ON CONFLICT (brand_id, name) DO UPDATE
                SET name = EXCLUDED.name
            RETURNING id
            """;
        for (Map<String, Object> row : list) {
            String  name        = String.valueOf(row.get("name")).trim();
            Integer jsonBrandId = toInt(row.get("brand_id"));     // из JSON
            Long    dbBrandId   = brandIdMap.get(jsonBrandId);
            if (dbBrandId == null) {
                throw new IllegalStateException("No brand mapping for json brand_id=" + jsonBrandId);
            }
            jdbc.queryForObject(sql, Long.class, dbBrandId, name);
        }
    }

    private Integer toInt(Object o) {
        if (o == null) return null;
        if (o instanceof Integer i) return i;
        if (o instanceof Long l)    return Math.toIntExact(l);
        if (o instanceof String s && !s.isBlank()) return Integer.parseInt(s.trim());
        return null;
    }

    private Integer parseYear(Object o) {
        Integer i = toInt(o);
        if (i != null) return i;
        throw new IllegalArgumentException("Invalid year: " + o);
    }
}

