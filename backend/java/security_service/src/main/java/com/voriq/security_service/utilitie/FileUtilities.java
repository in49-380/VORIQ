package com.voriq.security_service.utilitie;

import com.voriq.security_service.exception_handler.exception.ServerException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class FileUtilities {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("uuuu-MM-dd");

    public static Set<Path> getFileFromDir(Path dir, String pattern) {
        try (var ds = Files.newDirectoryStream(dir, pattern)) {
            Set<Path> logFiles = new HashSet<>();
            for (Path p : ds) {
                if (java.nio.file.Files.isRegularFile(p)) {
                    logFiles.add(p);
                }
            }
            return logFiles;
        } catch (IOException e) {
            throw new ServerException("I/O exception in dir: " + dir, e);
        }
    }
}
