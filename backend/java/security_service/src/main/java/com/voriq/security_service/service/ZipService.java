package com.voriq.security_service.service;

import com.voriq.security_service.service.interfaces.ArchiveService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ZipService implements ArchiveService {

    @Override
    public Path createArchive(Path outDir, String archiveName, Set<Path> files) throws IOException {
        return createArchive(outDir, archiveName, files, /*autoRenameIfExists*/ false);
    }

    @Override
    public Path createArchive(Path outDir, String archiveName, Set<Path> files, boolean autoRenameIfExists)
            throws IOException {

        if (outDir == null) outDir = Paths.get(".");
        Files.createDirectories(outDir);

        if (archiveName == null || archiveName.isBlank()) archiveName = "archive.zip";
        String fileName = ensureZipExtension(archiveName);

        Path archivePath = outDir.resolve(fileName).toAbsolutePath().normalize();
        if (autoRenameIfExists) {
            archivePath = uniquifyArchiveName(archivePath);
        }

        return writeZip(archivePath, files);
    }

    private static Path writeZip(Path archivePath, Set<Path> files) throws IOException {
        Path parent = archivePath.getParent();
        if (parent != null) Files.createDirectories(parent);

        if (files == null || files.isEmpty()) return archivePath;

        Path archiveAbs = archivePath.toAbsolutePath().normalize();
        LinkedHashSet<Path> normalized = files.stream()
                .filter(Objects::nonNull)
                .map(p -> p.toAbsolutePath().normalize())
                .filter(Files::isRegularFile)
                .filter(p -> !p.equals(archiveAbs))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (normalized.isEmpty()) return archivePath;

        Path base = commonRoot(normalized);
        Set<String> usedEntryNames = new HashSet<>();

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(archivePath), StandardCharsets.UTF_8)) {
            for (Path p : normalized) {
                String entryName = toEntryName(base, p);
                entryName = uniquifyEntry(entryName, usedEntryNames);

                ZipEntry entry = new ZipEntry(entryName);
                try {
                    entry.setTime(Files.getLastModifiedTime(p).toMillis());
                } catch (IOException ignore) {
                }
                zos.putNextEntry(entry);
                Files.copy(p, zos);
                zos.closeEntry();
            }
        }
        return archivePath;
    }

    private static String ensureZipExtension(String name) {
        String n = name.trim();
        return n.toLowerCase().endsWith(".zip") ? n : n + ".zip";
    }

    private static Path uniquifyArchiveName(Path path) {
        if (!Files.exists(path)) return path;

        String fileName = path.getFileName().toString();
        String name = fileName;
        String ext = "";
        int dot = fileName.lastIndexOf('.');
        if (dot > 0) {
            name = fileName.substring(0, dot);
            ext = fileName.substring(dot);
        }
        int i = 1;
        Path candidate;
        do {
            candidate = path.getParent().resolve(name + " (" + i + ")" + ext);
            i++;
        } while (Files.exists(candidate));
        return candidate;
    }

    private static Path commonRoot(Collection<Path> paths) {
        Iterator<Path> it = paths.iterator();
        if (!it.hasNext()) return null;
        Path root = it.next().getParent();
        while (it.hasNext() && root != null) {
            Path p = it.next().getParent();
            while (p != null && (root == null || !p.startsWith(root))) {
                root = (root == null) ? null : root.getParent();
            }
        }
        return root;
    }

    private static String toEntryName(Path base, Path file) {
        String name;
        if (base != null) {
            try {
                name = base.relativize(file).toString();
            } catch (IllegalArgumentException e) {
                name = file.getFileName().toString();
            }
        } else {
            name = file.getFileName().toString();
        }
        return name.replace('\\', '/');
    }

    private static String uniquifyEntry(String candidate, Set<String> used) {
        if (candidate == null || candidate.isBlank()) candidate = "unnamed";
        candidate = candidate.replace('\\', '/');
        while (candidate.startsWith("/")) candidate = candidate.substring(1);
        if (used.add(candidate)) return candidate;

        String dir = "";
        String base = candidate;
        int slash = candidate.lastIndexOf('/');
        if (slash >= 0) {
            dir = candidate.substring(0, slash + 1);
            base = candidate.substring(slash + 1);
        }

        String name = base, ext = "";
        int dot = base.lastIndexOf('.');
        if (dot > 0) {
            name = base.substring(0, dot);
            ext = base.substring(dot);
        }

        int i = 1;
        String attempt;
        do {
            attempt = dir + name + " (" + i + ")" + ext;
            i++;
        }
        while (!used.add(attempt));
        return attempt;
    }
}
