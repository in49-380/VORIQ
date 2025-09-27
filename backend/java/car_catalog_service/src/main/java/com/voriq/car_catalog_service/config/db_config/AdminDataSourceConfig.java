package com.voriq.car_catalog_service.config.db_config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * DataSource configuration that exposes an administrative {@link javax.sql.DataSource}
 * for the development environment.
 * <p>
 * This configuration connects to a PostgreSQL <em>admin database</em> defined by the
 * properties {@code app.pg.host}, {@code app.pg.port}, {@code app.pg.admin-db},
 * {@code app.pg.user}, and {@code app.pg.password}. It is intended for startup
 * and maintenance tasks (e.g., schema creation, migrations, seed scripts) that
 * require elevated privileges separate from the application’s primary data source.
 * </p>
 * <p>
 * The bean is created only when the {@code dev} Spring profile is active and uses
 * HikariCP with a small connection pool (maximum of 2 connections) to keep resource
 * usage minimal during development.
 * </p>
 *
 * @author RsLan
 * @since 1.0.0
 */
@Configuration
@Profile("dev")
public class AdminDataSourceConfig {

    @Value("${app.pg.host}")
    private String host;
    @Value("${app.pg.port}")
    private int port;
    @Value("${app.pg.admin-db}")
    private String adminDb;
    @Value("${app.pg.user}")
    private String user;
    @Value("${app.pg.password}")
    private String password;

    /**
     * Builds and exposes the administrative {@link DataSource} backed by {@link HikariDataSource}.
     * <p>
     * The resulting bean is named {@code adminDataSource} and connects to the configured
     * PostgreSQL admin database using the supplied credentials. The pool size is limited
     * to 2 connections to suit development needs.
     * </p>
     *
     * @return a configured {@link DataSource} for administrative operations
     * @author RsLan
     * @since 1.0.0
     */
    @Bean("adminDataSource")
    public DataSource adminDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:postgresql://%s:%d/%s".formatted(host, port, adminDb));
        ds.setUsername(user);
        ds.setPassword(password);
        ds.setMaximumPoolSize(2);
        return ds;
    }
}
