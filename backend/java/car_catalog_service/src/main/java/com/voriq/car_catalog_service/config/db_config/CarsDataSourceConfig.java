package com.voriq.car_catalog_service.config.db_config;


import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class CarsDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.cars")
    public DataSourceProperties carsDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource carsDataSource() {
        return carsDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    public JdbcTemplate carsJdbcTemplate(DataSource carsDataSource) {
        return new JdbcTemplate(carsDataSource);
    }
}
