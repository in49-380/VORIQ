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

/**
 * Periodic archiver and cleaner for log files that follow the naming pattern
 * {@code voriq_token.yyyy-MM-dd.log}. Creates ZIP archives for recent logs and
 * removes old archives on a monthly schedule.
 *
 * <p><strong>Responsibilities</strong>:</p>
 * <ul>
 *   <li>Every {@code archive.schedule.fixed-delay-ms} (default: 3 days) scans {@code ${log.dir}} for
 *       {@code *.log}, groups eligible files by month (current and previous), and creates ZIP archives
 *       named {@code voriq_token.<fromDate>_<toDate>.zip} (or {@code voriq_token.<date>.zip} if single day).</li>
 *   <li>On {@code 0 0 0 1 * *} (monthly, at midnight Berlin time) deletes ZIP archives older than one month.
 *       Archive files must match {@code voriq_token.<date>.zip} or {@code voriq_token.<date>_<date>.zip}.</li>
 *   <li>Safety check prevents deletion outside {@code ${log.dir}}.</li>
 * </ul>
 *
 * <p><strong>Configuration</strong>:</p>
 * <ul>
 *   <li>{@code log.dir} — base directory for logs and archives.</li>
 *   <li>{@code archive.schedule.fixed-delay-ms} — delay between archive runs (default 259200000 ms = 3 days).</li>
 *   <li>{@code archive.schedule.initial-delay-ms} — initial delay before first run (default 60000 ms).</li>
 * </ul>
 *
 * <p><strong>Notes</strong>:</p>
 * <ul>
 *   <li>Only logs strictly before today are considered. Files older than the fixed delay window are skipped.</li>
 *   <li>Archive creation errors are wrapped into {@link ServerException} with I/O cause.</li>
 *   <li>ZIP packing is delegated to {@link ZipService}.</li>
 * </ul>
 *
 * @author RsLan
 * @since 1.0.0
 * @see ZipService
 */
@Service
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class FilesZipper {

    private final ZipService zipService;
    private final Clock clock;

    /** Strict ISO date formatter: yyyy-MM-dd. */
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);

    /** Matches log files like: voriq_token.2025-08-19.log */
    private static final Pattern DATED =
            Pattern.compile("^voriq_token\\.(\\d{4}-\\d{2}-\\d{2})\\.log$");

    /** Matches archives like: voriq_token.2025-08-01.zip or voriq_token.2025-08-01_2025-08-03.zip */
    private static final Pattern ARCHIVE =
            Pattern.compile("(?i)voriq_token\\.(\\d{4}-\\d{2}-\\d{2})(?:_(\\d{4}-\\d{2}-\\d{2}))?\\.zip");

    @Value("${log.dir}")
    private String logDir;

    @Value("${archive.schedule.fixed-delay-ms:259200000}")
    private long fixedDelayMs;

    /**
     * Scans {@code ${log.dir}} for daily log files and creates ZIP archives for the current and
     * previous month, limited to files whose age is within the configured fixed delay window.
     *
     * <p>Scheduling:</p>
     * <ul>
     *   <li>{@code fixedDelayString = ${archive.schedule.fixed-delay-ms:259200000}} (default: 3 days)</li>
     *   <li>{@code initialDelayString = ${archive.schedule.initial-delay-ms:60000}} (default: 60 seconds)</li>
     *   <li>Zone: Europe/Berlin</li>
     * </ul>
     *
     * <p>On I/O failure, throws {@link ServerException}.</p>
     *
     * @throws ServerException if archive creation fails due to I/O.
     */
    @Scheduled(
            fixedDelayString = "${archive.schedule.fixed-delay-ms:259200000}",     // 3 days
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

            // Only process files strictly before today, and within the fixed-delay window
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
                    // ignore files older than previous month or outside window
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

    /**
     * Removes ZIP archives older than one month.
     *
     * <p>Scheduling:</p>
     * <ul>
     *   <li>Cron: {@code 0 0 0 1 * *} — at midnight on the 1st day of every month (Europe/Berlin).</li>
     * </ul>
     *
     * <p>Only files that match the naming pattern
     * {@code voriq_token.yyyy-MM-dd.zip} or {@code voriq_token.yyyy-MM-dd_yyyy-MM-dd.zip}
     * are considered. The month threshold is computed from the first date in the archive name.</p>
     */
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

    /**
     * Deletes the given {@code file} only if it resides under {@code baseDir}.
     * Logs the outcome at INFO/DEBUG/ERROR levels.
     *
     * @param baseDir base directory that bounds deletion.
     * @param file    file to delete.
     */
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

    /**
     * Builds an archive file name from a sorted set of dates.
     * <ul>
     *   <li>Single date → {@code voriq_token.<date>.zip}</li>
     *   <li>Multiple dates → {@code voriq_token.<first>_<last>.zip}</li>
     * </ul>
     *
     * @param bases sorted set of LocalDate entries.
     * @return archive file name or empty string if input is null/empty.
     */
    private String createZipArchiveFileName(SortedSet<LocalDate> bases) {
        if (bases == null || bases.isEmpty()) return "";
        StringBuilder name = new StringBuilder("voriq_token.")
                .append(bases.first().format(FMT));
        if (bases.size() > 1) {
            name.append("_").append(bases.last().format(FMT));
        }
        return name.append(".zip").toString();
    }

    /**
     * Extracts the base name between the first and last dots of the file name.
     * Used to parse dates from filenames.
     *
     * @param filePath file path.
     * @return base name suitable for date parsing.
     */
    private String getBaseFileName(Path filePath) {
        String name = filePath.getFileName().toString();
        int firstDot = name.indexOf('.');
        int lastDot = name.lastIndexOf('.');
        return (firstDot >= 0 && lastDot > firstDot)
                ? name.substring(firstDot + 1, lastDot)
                : (lastDot > 0) ? name.substring(0, lastDot) : name;
    }

    /**
     * Parses {@code LocalDate} from a log file name using {@link #FMT}.
     * On parse failure, logs an error and returns {@code null}.
     *
     * @param filePath log file path (e.g., {@code voriq_token.2025-08-19.log}).
     * @return parsed date or {@code null} if invalid.
     */
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

    /**
     * Parses the month (as first day) from an archive file name. Supports:
     * <ul>
     *   <li>{@code voriq_token.<date>.zip} → returns month of {@code <date>} with day=1</li>
     *   <li>{@code voriq_token.<date1>_<date2>.zip} → valid only if {@code date1} and {@code date2}
     *       are in the same month; returns that month with day=1</li>
     * </ul>
     *
     * @param filePath archive file path.
     * @return first day of the archive month, or {@code null} if the name is invalid.
     */
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
                // invalid parts -> null
            }
        }
        return null;
    }
}
