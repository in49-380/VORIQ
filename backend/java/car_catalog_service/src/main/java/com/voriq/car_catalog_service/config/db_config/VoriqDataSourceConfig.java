package com.voriq.car_catalog_service.config.db_config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class VoriqDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.voriq")
    public DataSourceProperties voriqDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @DependsOn("voriqDbInit")
    public DataSource voriqDataSource() {
        return voriqDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    public JdbcTemplate voriqJdbcTemplate(DataSource voriqDataSource) {
        return new JdbcTemplate(voriqDataSource);
    }
}

