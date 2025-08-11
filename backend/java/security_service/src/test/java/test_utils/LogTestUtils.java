package test_utils;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

public final class LogTestUtils {
    private LogTestUtils() {
    }

    @FunctionalInterface
    interface Check {
        boolean ok() throws Exception;
    }

    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String readLastNonEmptyLineFromLog(Path logPath, Charset charset) throws Exception {
        File file = logPath.toFile();
        if (!file.exists() || file.length() == 0) return null;

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            long pos = file.length() - 1;

            while (pos >= 0) {
                raf.seek(pos);
                int ch = raf.read();
                if (ch != '\n' && ch != '\r') break;
                pos--;
            }
            if (pos < 0) return null;

            StringBuilder sb = new StringBuilder();
            while (pos >= 0) {
                raf.seek(pos);
                int ch = raf.read();
                if (ch == '\n') break;
                if (ch != '\r') sb.append((char) ch);
                pos--;
            }
            return sb.reverse().toString();
        }
    }

    public static String getLastLineFromLog() throws Exception {
        Path logPath = todaysLog();

        waitFor(() -> Files.exists(logPath), Duration.ofSeconds(3), "log file not created");
        waitFor(() -> Files.size(logPath) > 0, Duration.ofSeconds(3), "log file is empty");

        return LogTestUtils.readLastNonEmptyLineFromLog(logPath, StandardCharsets.UTF_8);
    }

    public static void removeLastLogLine() throws Exception {
        Path logPath = todaysLog();

        List<String> allLines = Files.readAllLines(logPath, StandardCharsets.UTF_8);

        int lastIndex = -1;
        for (int i = allLines.size() - 1; i >= 0; i--) {
            if (!allLines.get(i).trim().isEmpty()) {
                lastIndex = i;
                break;
            }
        }
        if (lastIndex >= 0) {
            allLines.remove(lastIndex);
        }
        Files.write(logPath, allLines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static void waitFor(Check check, Duration timeout, String failMsg) throws Exception {
        long end = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < end) {
            if (check.ok()) return;
            Thread.sleep(50);
        }
        fail(failMsg);
    }

    private static Path todaysLog() {
        String name = "logs/voriq_token." + LocalDate.now().format(DAY) + ".log";
        return Path.of(name);
    }
}