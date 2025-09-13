package com.voriq.car_catalog_service.config.db_config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Bootstraps the application database for the development profile.
 * <p>
 * Responsibilities:
 * <ul>
 *     <li>Ensures the target database exists (creates it via the administrative {@link DataSource}
 *     if missing).</li>
 *     <li>Detects whether the database is already initialized by probing for well-known tables.</li>
 *     <li>If not initialized, imports a PostgreSQL dump from the classpath
 *     ({@code db/dump-voriq_cars.sql}) by executing collected DDL statements, streaming
 *     {@code COPY ... FROM STDIN} data via {@link CopyManager}, and then applying
 *     foreign-key constraints.</li>
 * </ul>
 * <p>
 * This configuration is only active for the {@code dev} Spring profile and runs once at
 * context startup by exposing a no-op bean named {@code voriqDbInit}.
 * </p>
 *
 * @author RsLan
 * @since 1.0.0
 */
@Slf4j
@Configuration("voriqDbInitConfig")
@RequiredArgsConstructor
@Profile("dev")
public class DatabaseBootstrap {

    private final @Qualifier("adminDataSource") DataSource adminDataSource;

    @Value("${app.pg.host}")
    private String host;
    @Value("${app.pg.port}")
    private int port;
    @Value("${app.pg.user}")
    private String user;
    @Value("${app.pg.password}")
    private String password;
    @Value("${app.pg.app-db}")
    private String appDb;

    private static final String DUMP_CLASSPATH = "db/dump-voriq_cars.sql";

    private String dbLower() {
        return appDb == null ? null : appDb.toLowerCase(java.util.Locale.ROOT);
    }

    /**
     * Triggers the database bootstrap process when the Spring context is created.
     * <p>
     * Flow:
     * <ol>
     *     <li>Create the target database if it does not already exist.</li>
     *     <li>Check whether the database is initialized.</li>
     *     <li>If not initialized, import the bundled dump into the application database.</li>
     * </ol>
     * The returned {@link Object} has no functional meaning; it simply ensures the
     * configuration executes at startup.
     * </p>
     *
     * @return a dummy bean instance to run the initialization during context startup
     * @throws Exception if dump parsing or import fails
     * @author RsLan
     * @since 1.0.0
     */
    @Bean("voriqDbInit")
    public Object bootstrap() throws Exception {
        createDatabaseIfMissing();
        if (!isAlreadyInitialized()) {
            importDumpIntoAppDatabase();
        } else {
            log.info("Database {} is already initialized, skipping import", dbLower());
        }
        return new Object();
    }

    /**
     * Creates the application database if it does not already exist.
     * <p>
     * Uses the administrative {@link DataSource} to query {@code pg_database}. If missing,
     * executes a {@code CREATE DATABASE} statement with deterministic locale and encoding.
     * PostgreSQL SQLSTATE {@code 42P04} (duplicate_database) is treated as a benign race.
     * </p>
     *
     * @author RsLan
     * @since 1.0.0
     */
    private void createDatabaseIfMissing() {
        JdbcTemplate admin = new JdbcTemplate(adminDataSource);
        String db = dbLower();

        Integer cnt = admin.queryForObject(
                "SELECT COUNT(*) FROM pg_database WHERE lower(datname) = ?",
                Integer.class, db
        );
        if (cnt != null && cnt > 0) {
            log.info("Database {} already exists", db);
            return;
        }

        log.info("Creating database {} ...", db);
        String sql = ("""
                CREATE DATABASE %s
                  TEMPLATE template0
                  ENCODING 'UTF8'
                  LOCALE_PROVIDER 'libc'
                  LC_COLLATE 'C'
                  LC_CTYPE 'C'
                """).formatted(db);

        try {
            admin.execute(sql);
            log.info("Database {} created", db);
        } catch (org.springframework.jdbc.BadSqlGrammarException e) {
            Throwable cause = e.getCause();
            if (cause instanceof org.postgresql.util.PSQLException pg
                    && "42P04".equals(pg.getSQLState())) {
                log.info("Database {} already exists (race), skipping CREATE", db);
            } else {
                throw e;
            }
        }
    }

    /**
     * Determines whether the database already contains expected application tables.
     * <p>
     * The check queries {@code information_schema.tables} for a small set of known table
     * names within the {@code public} schema. Any positive match is treated as "initialized".
     * Returns {@code false} if an exception occurs while probing.
     * </p>
     *
     * @return {@code true} if at least one expected table exists; otherwise {@code false}
     * @author RsLan
     * @since 1.0.0
     */
    private boolean isAlreadyInitialized() {
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbLower();
        String sql = """
                select 1
                from information_schema.tables
                where table_schema='public'
                  and table_name in (
                    'cars-brand','cars-carmodel','cars-engine','cars-fueltype','cars-year',
                    'cars-car','auth_group','auth_user','django_migrations'
                  )
                limit 1
                """;
        try (Connection c = DriverManager.getConnection(url, user, password);
             Statement st = c.createStatement();
             var rs = st.executeQuery(sql)) {
            return rs.next();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Imports the SQL dump into the application database.
     * <p>
     * Steps:
     * <ol>
     *     <li>Ensure {@code public} schema exists and set the search path.</li>
     *     <li>Parse the dump into DDL statements, COPY blocks, and FK constraints.</li>
     *     <li>Execute DDL statements.</li>
     *     <li>Stream data for each COPY block using PostgreSQL's {@link CopyManager}.</li>
     *     <li>Apply foreign-key constraints.</li>
     * </ol>
     * Logging includes the first line of each executed statement and basic COPY stats.
     * </p>
     *
     * @throws Exception if any parsing or database operation fails
     * @author RsLan
     * @since 1.0.0
     */
    private void importDumpIntoAppDatabase() throws Exception {
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbLower();

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            try (Statement pre = conn.createStatement()) {
                pre.execute("CREATE SCHEMA IF NOT EXISTS public");
                pre.execute("SET search_path = public");
            }

            PGConnection pgConn = conn.unwrap(PGConnection.class);
            CopyManager copyManager = pgConn.getCopyAPI();

            List<String> ddlStatements = new ArrayList<>();
            List<CopyBlock> copyBlocks = new ArrayList<>();
            List<String> fkConstraints = new ArrayList<>();

            parseDump(ddlStatements, copyBlocks, fkConstraints);

            try (Statement st = conn.createStatement()) {
                for (String ddl : ddlStatements) {
                    log.info("DDL: {}", firstLine(ddl));
                    st.execute(ddl);
                }
            }

            for (CopyBlock cb : copyBlocks) {
                String copySql = "COPY " + cb.qualifiedTable +
                        " (" + String.join(", ", cb.columns) + ") FROM STDIN";
                log.info("COPY {} ({} rows)", cb.qualifiedTable, cb.rows.size());

                StringBuilder buf = new StringBuilder();
                for (String row : cb.rows) buf.append(row).append('\n');

                try (Reader reader = new StringReader(buf.toString())) {
                    copyManager.copyIn(copySql, reader);
                }
            }

            try (Statement st = conn.createStatement()) {
                for (String fk : fkConstraints) {
                    log.info("FK: {}", firstLine(fk));
                    st.execute(fk);
                }
            }

            log.info("✅ Import into {} completed", dbLower());
        }
    }

    /**
     * Parses a PostgreSQL dump into executable units.
     * <p>
     * The parser scans the dump file for:
     * <ul>
     *     <li>{@code CREATE TABLE ...;} blocks → collected as DDL statements,</li>
     *     <li>{@code COPY schema.table (cols...) FROM stdin;} blocks with data rows
     *     terminated by {@code \.} → collected as {@link CopyBlock}s,</li>
     *     <li>{@code ALTER TABLE ONLY ... ADD CONSTRAINT ...;} → collected as FK constraints.</li>
     * </ul>
     * Several non-essential lines (role/db settings, {@code \connect}, etc.) are ignored.
     * </p>
     *
     * @param ddlStatements output list for DDL statements (in discovery order)
     * @param copyBlocks    output list for COPY blocks with rows
     * @param fkConstraints output list for foreign-key constraint statements
     * @throws IOException if the dump resource cannot be read
     * @author RsLan
     * @since 1.0.0
     */
    private void parseDump(List<String> ddlStatements,
                           List<CopyBlock> copyBlocks,
                           List<String> fkConstraints) throws IOException {

        ClassPathResource res = new ClassPathResource(DUMP_CLASSPATH);
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            boolean inCreate = false;
            StringBuilder ddl = new StringBuilder();

            boolean inCopy = false;
            CopyBlock current = null;

            while ((line = br.readLine()) != null) {
                String t = line.trim();

                if (t.isEmpty()
                        || t.startsWith("-- Roles")
                        || t.startsWith("-- Databases")
                        || t.startsWith("\\connect")
                        || t.startsWith("CREATE ROLE")
                        || t.startsWith("ALTER ROLE")
                        || t.startsWith("CREATE DATABASE ")
                        || t.startsWith("ALTER DATABASE ")
                        || t.startsWith("SET ")
                        || t.startsWith("SELECT pg_catalog.set_config")) {
                    continue;
                }

                if (t.startsWith("CREATE TABLE ")) {
                    inCreate = true;
                    ddl.setLength(0);
                }
                if (inCreate) {
                    ddl.append(line).append('\n');
                    if (t.endsWith(");")) {
                        inCreate = false;
                        ddlStatements.add(ddl.toString());
                        ddl.setLength(0);
                    }
                    continue;
                }

                if (!inCopy && t.startsWith("COPY ") && t.contains(" FROM stdin;")) {
                    inCopy = true;
                    current = new CopyBlock();

                    String head = t.substring(5, t.indexOf(" FROM stdin;")).trim();
                    int p = head.indexOf('(');
                    String table = (p > 0) ? head.substring(0, p).trim() : head;
                    String cols = head.substring(p + 1, head.lastIndexOf(')'));

                    current.qualifiedTable = table;
                    current.columns = Arrays.stream(cols.split(","))
                            .map(String::trim)
                            .toList();
                    continue;
                }
                if (inCopy) {
                    if (t.equals("\\.")) {
                        inCopy = false;
                        copyBlocks.add(current);
                        current = null;
                    } else {
                        current.rows.add(line);
                    }
                    continue;
                }

                if (t.startsWith("ALTER TABLE ONLY") && t.contains("ADD CONSTRAINT")) {
                    fkConstraints.add(line);
                }
            }
        }
    }

    /**
     * Returns the first non-blank line of the given string (or the whole string if single-line).
     *
     * @param s source text
     * @return the first line, trimmed of surrounding whitespace
     * @author RsLan
     * @since 1.0.0
     */
    private static String firstLine(String s) {
        String l = s.strip();
        int i = l.indexOf('\n');
        return i > 0 ? l.substring(0, i) : l;
    }

    /**
     * Container for a parsed {@code COPY} block consisting of a fully-qualified table name,
     * the ordered list of column names, and the raw data rows to be piped to {@code STDIN}.
     *
     * @author RsLan
     * @since 1.0.0
     */
    private static class CopyBlock {
        String qualifiedTable;
        List<String> columns = new ArrayList<>();
        List<String> rows = new ArrayList<>();
    }
}
