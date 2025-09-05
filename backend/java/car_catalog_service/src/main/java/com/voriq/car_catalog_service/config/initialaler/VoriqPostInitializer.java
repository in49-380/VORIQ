package com.voriq.car_catalog_service.config.initialaler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;

@Slf4j
@Component
@Profile("dev")
@DependsOn("voriqDbInit")
public class VoriqPostInitializer implements ApplicationRunner {

    private final JdbcTemplate voriqJdbc;

    public VoriqPostInitializer(@Qualifier("voriqJdbcTemplate") JdbcTemplate voriqJdbc) {
        this.voriqJdbc = voriqJdbc;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        runScript("db/dev-fill-voriq-catalogs.sql");
        runScript("db/dev-build-cars-car.sql");
        log.info("✅ VORIQ_cars post-init (dev) completed.");
    }

    private void runScript(String path) throws Exception {
        ClassPathResource res = new ClassPathResource(path);
        if (!res.exists()) {
            log.info("Script {} not found — skipping.", path);
            return;
        }
        try (Connection con = voriqJdbc.getDataSource().getConnection()) {
            ScriptUtils.executeSqlScript(con, new EncodedResource(res, StandardCharsets.UTF_8));
            log.info("✅ Executed {}", path);
        }
    }
}
