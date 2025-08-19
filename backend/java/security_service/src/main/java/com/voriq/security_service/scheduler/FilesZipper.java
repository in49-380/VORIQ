package com.voriq.security_service.scheduler;

import com.voriq.security_service.exception_handler.exception.ServerException;
import com.voriq.security_service.service.ZipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.voriq.security_service.utilitie.FileUtilities.getFileFromDir;

@Service
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class FilesZipper {

    private final ZipService zipService;
    private final Clock clock;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);

    private static final Pattern DATED =
            Pattern.compile("^voriq_token\\.(\\d{4}-\\d{2}-\\d{2})\\.log$");

    private static final Pattern ARCHIVE =
            Pattern.compile("(?i)voriq_token\\.(\\d{4}-\\d{2}-\\d{2})(?:_(\\d{4}-\\d{2}-\\d{2}))?\\.zip");


    @Value("${log.dir}")
    private String logDir;

    @Value("${archive.schedule.fixed-delay-ms:259200000}")
    private long fixedDelayMs;

    @Scheduled(
            fixedDelayString = "${archive.schedule.fixed-delay-ms:259200000}",     // 3days
            initialDelayString = "${archive.schedule.initial-delay-ms:60000}",     // 60s
            zone = "Europe/Berlin"
    )
    public void zipFiles() {
        Path zipPath = Path.of(logDir);

        Set<Path> filePaths = getFileFromDir(zipPath, "*.log");

        if (filePaths.isEmpty()) return;

        Set<Path> curr = new HashSet<>();
        Set<Path> prev = new HashSet<>();
        SortedSet<LocalDate> currDates = new TreeSet<>();
        SortedSet<LocalDate> prevDates = new TreeSet<>();

        LocalDate today = LocalDate.now(clock);

        for (Path p : filePaths) {
            Matcher m = DATED.matcher(p.getFileName().toString());
            if (!m.matches()) continue;
            LocalDate fileDate = getFileDateFromFileName(p);
            if (fileDate == null) continue;

            if (!fileDate.isBefore(today)) continue;
            long ageMs = Duration.between(
                    fileDate.atStartOfDay(clock.getZone()).toInstant(),
                    today.atStartOfDay(clock.getZone()).toInstant()
            ).toMillis();
            if (ageMs > fixedDelayMs) continue;

            int monthsDiff = (today.getYear() - fileDate.getYear()) * 12
                    + (today.getMonthValue() - fileDate.getMonthValue());

            switch (monthsDiff) {
                case 0 -> {
                    curr.add(p);
                    currDates.add(fileDate);
                }
                case 1 -> {
                    prev.add(p);
                    prevDates.add(fileDate);
                }
                default -> {
                }
            }
        }

        try {
            if (!curr.isEmpty()) {
                String name = createZipArchiveFileName(currDates);
                if (!name.isBlank()) zipService.createArchive(Path.of(logDir), name, curr);
            }
            if (!prev.isEmpty()) {
                String name = createZipArchiveFileName(prevDates);
                if (!name.isBlank()) zipService.createArchive(Path.of(logDir), name, prev);
            }
        } catch (IOException e) {
            throw new ServerException("I/O exception in dir: " + Path.of(logDir), e);
        }
    }

    @Scheduled(cron = "0 0 0 1 * *", zone = "Europe/Berlin")
    public void removeOldZipFiles() {
        Path zipPath = Path.of(logDir);

        Set<Path> filePaths = getFileFromDir(zipPath, "*.zip");

        if (filePaths.isEmpty()) return;

        LocalDate today = LocalDate.now(clock);

        for (Path p : filePaths) {
            Matcher m = ARCHIVE.matcher(p.getFileName().toString());
            if (!m.matches()) continue;

            LocalDate firstDayOfArchiveMonth = getZipFileDateFromFileName(p);
            if (firstDayOfArchiveMonth == null) continue;

            int monthsDiff = (today.getYear() - firstDayOfArchiveMonth.getYear()) * 12
                    + (today.getMonthValue() - firstDayOfArchiveMonth.getMonthValue());
            if (monthsDiff > 1) {
                deleteSafely(Path.of(logDir), p);
            }
        }
    }

    private void deleteSafely(Path baseDir, Path file) {
        Path absBase = baseDir.toAbsolutePath().normalize();
        Path absFile = file.toAbsolutePath().normalize();

        if (!absFile.startsWith(absBase)) {
            log.warn("Skip delete outside base dir: {}", absFile);
            return;
        }

        try {
            boolean deleted = Files.deleteIfExists(absFile);
            if (deleted) {
                log.info("Deleted old log/archive: {}", absFile.getFileName());
            } else {
                log.debug("Nothing to delete (already missing): {}", absFile.getFileName());
            }
        } catch (java.io.IOException e) {
            log.error("Failed to delete file: {}", absFile, e);
        }
    }

    private String createZipArchiveFileName(SortedSet<LocalDate> bases) {
        if (bases == null || bases.isEmpty()) return "";
        StringBuilder name = new StringBuilder("voriq_token.")
                .append(bases.first().format(FMT));
        if (bases.size() > 1) {
            name.append("_").append(bases.last().format(FMT));
        }
        return name.append(".zip").toString();
    }

    private String getBaseFileName(Path filePath) {
        String name = filePath.getFileName().toString();
        int firstDot = name.indexOf('.');
        int lastDot = name.lastIndexOf('.');
        return (firstDot >= 0 && lastDot > firstDot)
                ? name.substring(firstDot + 1, lastDot)
                : (lastDot > 0) ? name.substring(0, lastDot) : name;
    }

    private LocalDate getFileDateFromFileName(Path filePath) {
        String baseDate = getBaseFileName(filePath);
        try {
            return LocalDate.parse(baseDate, FMT);
        } catch (Exception e) {
            log.error("[ERROR] {} Zip archive not created for file '{}'. Bad file name. Message: {}",
                    LocalDateTime.now(), filePath.getFileName(), e.getMessage());
            return null;
        }
    }

    private LocalDate getZipFileDateFromFileName(Path filePath) {
        String baseZipDate = getBaseFileName(filePath);

        int first = baseZipDate.indexOf('_');
        if (first < 0) {
            LocalDate d = getFileDateFromFileName(filePath);
            return d != null ? d.withDayOfMonth(1) : null;
        }

        int last = baseZipDate.lastIndexOf('_');
        if (first == last) {

            String[] parts = baseZipDate.split("_", -1);
            if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) return null;

            try {
                LocalDate d1 = LocalDate.parse(parts[0], FMT).withDayOfMonth(1);
                LocalDate d2 = LocalDate.parse(parts[1], FMT).withDayOfMonth(1);
                return d1.equals(d2) ? d1 : null;
            } catch (Exception ignore) {
            }
        }
        return null;
    }
}
