package com.voriq.security_service.scheduler;

import com.voriq.security_service.service.ZipService;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Set;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(value = DisplayNameGenerator.ReplaceUnderscores.class)
class FilesZipperUnitTest {

    @TempDir
    private Path tmp;

    @Mock
    private ZipService zipService;

    @Test
    void zips_only_prev_and_current_month_within_window() throws Exception {

        Clock clock = Clock.fixed(LocalDate.of(2025, 8, 3).atStartOfDay(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault());

        Path d = tmp;

        Path fCur1 = touch(d, "voriq_token.2025-08-01.log", "cur").getFileName();
        Path fCur2 = touch(d, "voriq_token.2025-08-02.log", "cur").getFileName();
        Path fPrev = touch(d, "voriq_token.2025-07-31.log", "prev").getFileName();
        touch(d, "voriq_token.2025-06-01.log", "too-old");

        FilesZipper zipper = new FilesZipper(zipService, clock);
        Field logDirF = FilesZipper.class.getDeclaredField("logDir");
        logDirF.setAccessible(true);
        logDirF.set(zipper, d.toString());

        Field windowF = FilesZipper.class.getDeclaredField("fixedDelayMs");
        windowF.setAccessible(true);
        windowF.setLong(zipper, 259200000L);

        zipper.zipFiles();

        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Set<Path>> setCaptor = ArgumentCaptor.forClass((Class) Set.class);

        verify(zipService, times(2))
                .createArchive(eq(d), nameCaptor.capture(), setCaptor.capture());
        verifyNoMoreInteractions(zipService);

        var names = nameCaptor.getAllValues();
        assertEquals(2, names.size());
        assertTrue(names.contains("voriq_token.2025-08-01_2025-08-02.zip"));
        assertTrue(names.contains("voriq_token.2025-07-31.zip"));

        var sets = setCaptor.getAllValues();
        assertEquals(2, sets.size());

        for (int i = 0; i < names.size(); i++) {
            String n = names.get(i);
            Set<Path> s = sets.get(i);

            if (n.equals("voriq_token.2025-08-01_2025-08-02.zip")) {
                assertEquals(2, s.size());
                assertTrue(s.stream().map(Path::getFileName).anyMatch(fCur1::equals));
                assertTrue(s.stream().map(Path::getFileName).anyMatch(fCur2::equals));
            } else if (n.equals("voriq_token.2025-07-31.zip")) {
                assertEquals(1, s.size());
                assertTrue(s.stream().map(Path::getFileName).anyMatch(fPrev::equals));
            } else {
                fail("Unexpected name of the archive: " + n);
            }
        }
    }

    @Test
    void removeOldZipFile_deletes_only_archives_older_than_one_month() throws Exception {
        ZoneId zone = ZoneId.of("Europe/Berlin");
        Clock clock = Clock.fixed(LocalDate.of(2025, 8, 18).atStartOfDay(zone).toInstant(), zone);

        FilesZipper zipper = new FilesZipper(zipService, clock);

        Field fLogDir = FilesZipper.class.getDeclaredField("logDir");
        fLogDir.setAccessible(true);
        fLogDir.set(zipper, tmp.toString());

        touch(tmp, "voriq_token.2025-06-15.zip");
        touch(tmp, "voriq_token.2025-07-31.zip");
        touch(tmp, "voriq_token.2025-08-01_2025-08-10.zip");
        touch(tmp, "voriq_token.2025-06-30_2025-07-02.zip");
        touch(tmp, "foo.zip");
        touch(tmp, "voriq_token.log");

        zipper.removeOldZipFiles();

        assertFalse(Files.exists(tmp.resolve("voriq_token.2025-06-15.zip")), "June archive should be deleted.");
        assertTrue(Files.exists(tmp.resolve("voriq_token.2025-07-31.zip")));
        assertTrue(Files.exists(tmp.resolve("voriq_token.2025-08-01_2025-08-10.zip")));
        assertTrue(Files.exists(tmp.resolve("voriq_token.2025-06-30_2025-07-02.zip")));
        assertTrue(Files.exists(tmp.resolve("foo.zip")));
        assertTrue(Files.exists(tmp.resolve("voriq_token.log")));
    }

    private static Path touch(Path dir, String name, CharSequence csq) throws Exception {
        return Files.writeString(
                dir.resolve(name),
                csq,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    private static Path touch(Path dir, String name) throws Exception {
        return touch(dir, name, "");
    }
}