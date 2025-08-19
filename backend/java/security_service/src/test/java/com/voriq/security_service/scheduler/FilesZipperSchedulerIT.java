package com.voriq.security_service.scheduler;

import com.voriq.security_service.service.ZipService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(value = DisplayNameGenerator.ReplaceUnderscores.class)
class FilesZipperSchedulerIT {

    @MockitoBean
    private ZipService zipService;

    @Autowired
    private FilesZipper filesZipper;

    private static Path LOGS_DIR;

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        try {
            LOGS_DIR = Files.createTempDirectory("files-zipper-it-");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        r.add("log.dir", () -> LOGS_DIR.toAbsolutePath().normalize().toString());
        r.add("archive.schedule.fixed-delay-ms", () ->
                String.valueOf(TimeUnit.DAYS.toMillis(31)));
        r.add("archive.schedule.initial-delay-ms", () -> "5000");
    }

    @AfterAll
    static void cleanupAll() throws Exception {
        if (LOGS_DIR != null) {
            Files.walk(LOGS_DIR)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (Exception ignore) {
                        }
                    });
        }
    }

    private Path dirFromBean() {
        try {
            Field f = FilesZipper.class.getDeclaredField("logDir");
            f.setAccessible(true);
            String v = (String) f.get(filesZipper);
            return Path.of(v).toAbsolutePath().normalize();
        } catch (Exception e) {
            throw new RuntimeException("Cannot read logDir from FilesZipper bean", e);
        }
    }

    @Nested
    @DisplayName("Zip Files ")
    class ZipFilesTest {

        @BeforeEach
        void prepare() throws Exception {
            Path dir = dirFromBean();
            Files.createDirectories(dir);
            try (var ds = Files.newDirectoryStream(dir)) {
                for (var p : ds) Files.deleteIfExists(p);
            }
            var FMT = DateTimeFormatter.ofPattern("uuuu-MM-dd");
            var yesterday = LocalDate.now(ZoneId.of("Europe/Berlin")).minusDays(1).format(FMT);
            Files.writeString(dir.resolve("voriq_token." + yesterday + ".log"), "cur\n");
        }

        @Test
        void scheduled_method_is_triggered_and_calls_ZipService() {
            Path dir = dirFromBean(); // !!!
            await().atMost(Duration.ofSeconds(8))
                    .untilAsserted(() ->
                            verify(zipService, atLeastOnce())
                                    .createArchive(eq(dir), anyString(), anySet())
                    );
        }
    }

    @Nested
    @DisplayName("Remove old zip files")
    class RemoveOldZipFilesTest {

        @TestConfiguration
        static class TestCfg {
            @Bean
            @Primary
            Clock testClock() {
                ZoneId zone = ZoneId.of("Europe/Berlin");
                return Clock.fixed(LocalDate.of(2025, 8, 18).atStartOfDay(zone).toInstant(), zone);
            }
        }

        @BeforeEach
        void setUp() throws Exception {
            Path dir = dirFromBean();
            Files.createDirectories(dir);
            try (var ds = Files.newDirectoryStream(dir)) {
                for (var p : ds) Files.deleteIfExists(p);
            }
            Files.createFile(dir.resolve("voriq_token.2025-05-10.zip"));
            Files.createFile(dir.resolve("voriq_token.2025-07-01_2025-07-31.zip"));
            Files.createFile(dir.resolve("voriq_token.2025-08-01.zip"));
            Files.createFile(dir.resolve("random.zip"));
            Files.writeString(dir.resolve("voriq_token.log"), "active\n");
        }

        @Test
        void removeOldZipFile_deletes_only_older_than_one_month() {

            filesZipper.removeOldZipFiles();

            Path dir = dirFromBean();
            assertFalse(Files.exists(dir.resolve("voriq_token.2025-05-10.zip")), "Old archive must be deleted");
            assertTrue(Files.exists(dir.resolve("voriq_token.2025-07-01_2025-07-31.zip")), "July archive should remain");
            assertTrue(Files.exists(dir.resolve("voriq_token.2025-08-01.zip")), "Current month should stay");
            assertTrue(Files.exists(dir.resolve("random.zip")), "Inappropriate files are not affected");
            assertTrue(Files.exists(dir.resolve("voriq_token.log")), "Active log do not touch");
        }
    }
}
