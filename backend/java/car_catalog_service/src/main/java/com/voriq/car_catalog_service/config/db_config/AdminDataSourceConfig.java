package com.voriq.car_catalog_service.config.db_config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
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

