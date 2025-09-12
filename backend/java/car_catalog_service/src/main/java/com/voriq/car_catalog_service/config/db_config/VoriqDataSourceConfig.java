package com.voriq.car_catalog_service.config.db_config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Centralized configuration for the application's primary {@link DataSource}
 * and its companion {@link JdbcTemplate}.
 * <p>
 * The configuration binds properties under {@code spring.datasource.voriq.*}
 * to a {@link DataSourceProperties} bean and uses it to construct the
 * {@code voriqDataSource}. Two variants of the data source are provided:
 * </p>
 * <ul>
 *   <li><b>dev profile:</b> the bean named {@code voriqDataSource} is created
 *   only after the {@code voriqDbInit} bootstrap bean completes, ensuring the
 *   database has been created/seeded before connections are opened.</li>
 *   <li><b>non-dev profiles:</b> the same bean name is exposed without any
 *   dependency on initialization.</li>
 * </ul>
 * <p>
 * A {@link JdbcTemplate} wired to {@code voriqDataSource} is also exposed for
 * convenient JDBC access.
 * </p>
 *
 * @author RsLan
 * @since 1.0.0
 */
@Configuration
public class VoriqDataSourceConfig {

    /**
     * Binds {@code spring.datasource.voriq.*} properties to a {@link DataSourceProperties}
     * instance used to build the primary application {@link DataSource}.
     *
     * @return configured {@link DataSourceProperties} for the Voriq data source
     * @author RsLan
     * @since 1.0.0
     */
    @Bean
    @ConfigurationProperties("spring.datasource.voriq")
    public DataSourceProperties voriqDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * Constructs the {@code voriqDataSource} for the {@code dev} profile and ensures it
     * is created only after the database bootstrap bean {@code voriqDbInit} has run.
     *
     * @return a {@link DataSource} built from {@link #voriqDataSourceProperties()}
     * @author RsLan
     * @since 1.0.0
     */
    @Bean(name = "voriqDataSource")
    @Profile("dev")
    @DependsOn("voriqDbInit")
    public DataSource voriqDataSourceDev() {
        return voriqDataSourceProperties().initializeDataSourceBuilder().build();
    }

    /**
     * Constructs the {@code voriqDataSource} for all non-{@code dev} profiles without
     * depending on any database bootstrap.
     *
     * @return a {@link DataSource} built from {@link #voriqDataSourceProperties()}
     * for non-development profiles only
     * @author RsLan
     * @since 1.0.0
     */
    @Bean(name = "voriqDataSource")
    @Profile("!dev")
    public DataSource voriqDataSource() {
        return voriqDataSourceProperties().initializeDataSourceBuilder().build();
    }

    /**
     * Exposes a {@link JdbcTemplate} backed by the {@code voriqDataSource}.
     *
     * @param voriqDataSource the primary application {@link DataSource}
     * @return a {@link JdbcTemplate} for convenient JDBC operations
     * @author RsLan
     * @since 1.0.0
     */
    @Bean
    public JdbcTemplate voriqJdbcTemplate(DataSource voriqDataSource) {
        return new JdbcTemplate(voriqDataSource);
    }
}
