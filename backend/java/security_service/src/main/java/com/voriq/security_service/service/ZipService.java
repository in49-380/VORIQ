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

/**
 * {@code ZipService} creates ZIP archives from a set of files.
 *
 * <p><strong>Key behaviors</strong>:</p>
 * <ul>
 *   <li>Ensures the archive name has a {@code .zip} extension.</li>
 *   <li>Optionally auto-renames the output file if a file with the same name already exists
 *       (e.g., {@code file.zip} → {@code file (1).zip}).</li>
 *   <li>Ignores {@code null} paths and non-regular files; also prevents adding the archive
 *       file itself into the archive.</li>
 *   <li>Computes a common parent directory and stores entry names relatively to it to keep
 *       a compact directory structure inside the ZIP.</li>
 *   <li>Guarantees unique entry names inside the archive (appends {@code (1)}, {@code (2)}, ... if needed).</li>
 *   <li>Sets the ZIP entry modification time (if retrievable from the file system).</li>
 * </ul>
 *
 * <p><strong>Notes & limitations</strong>:</p>
 * <ul>
 *   <li>If the provided {@code files} set is {@code null}, empty, or contains no regular files,
 *       this service returns the intended archive path <em>without creating the archive file</em>.</li>
 *   <li>Character encoding for the ZIP output entries is UTF-8.</li>
 *   <li>This class is stateless; methods are safe to call concurrently for different output files.
 *       Concurrency to the same target path should be avoided.</li>
 * </ul>
 *
 * <p><strong>Example</strong>:</p>
 * <pre>{@code
 * Set<Path> inputs = Set.of(Path.of("logs/app.2025-08-18.log"), Path.of("logs/app.2025-08-19.log"));
 * Path out = Path.of("logs/archive");
 * Path zip = zipService.createArchive(out, "logs-aug.zip", inputs, true);
 * }</pre>
 * <p>
 *
 * @author RsLan
 * @since 1.0.0
 */
@Service
public class ZipService implements ArchiveService {

    /**
     * Creates a ZIP archive at {@code outDir/archiveName} containing the given files.
     *
     * <p>Behavioral defaults:</p>
     * <ul>
     *   <li>{@code outDir == null} → current directory (".").</li>
     *   <li>{@code archiveName} is blank or {@code null} → {@code "archive.zip"}.</li>
     *   <li>If {@code archiveName} has no {@code .zip} extension, it's appended.</li>
     *   <li>If a file with that name already exists, it will be overwritten
     *       (use the {@link #createArchive(Path, String, Set, boolean)} overload with
     *       {@code autoRenameIfExists=true} to avoid overwriting).</li>
     *   <li>If {@code files} is {@code null}/empty or no regular files are found, the
     *       method returns the path but does <em>not</em> create a ZIP file.</li>
     * </ul>
     *
     * @param outDir      output directory for the archive (may be {@code null}).
     * @param archiveName file name for the archive; {@code .zip} is ensured (may be blank).
     * @param files       set of input files to include; non-regular files are ignored.
     * @return path to the archive (intended location). The file may not exist if no inputs.
     * @throws IOException if I/O fails creating directories or writing the ZIP.
     */
    @Override
    public Path createArchive(Path outDir, String archiveName, Set<Path> files) throws IOException {
        return createArchive(outDir, archiveName, files, /*autoRenameIfExists*/ false);
    }

    /**
     * Creates a ZIP archive at {@code outDir/archiveName} containing the given files.
     *
     * <p>Additional behavior compared to the simpler overload:</p>
     * <ul>
     *   <li>If {@code autoRenameIfExists} is {@code true}, and the target file exists,
     *       the service finds a unique name by appending {@code " (n)"} before the extension.</li>
     * </ul>
     *
     * @param outDir             output directory for the archive (may be {@code null}).
     * @param archiveName        file name for the archive; {@code .zip} is ensured (may be blank).
     * @param files              set of input files to include; non-regular files are ignored.
     * @param autoRenameIfExists when {@code true}, avoids overwriting existing file by auto-renaming.
     * @return path to the archive (final location). The file may not exist if no inputs.
     * @throws IOException if I/O fails creating directories or writing the ZIP.
     */
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

    /**
     * Writes the ZIP file to {@code archivePath} from {@code files}.
     *
     * <p>Filters input to regular files only; prevents adding the target archive itself.
     * If no valid files remain, returns the path without creating a ZIP.</p>
     *
     * @param archivePath absolute path to the target ZIP file.
     * @param files       candidate files to include (may be {@code null}/empty).
     * @return {@code archivePath} (the file may not exist if no inputs).
     * @throws IOException if I/O fails while writing the ZIP.
     */
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
                    // If last modified time cannot be read, proceed without setting it.
                }
                zos.putNextEntry(entry);
                Files.copy(p, zos);
                zos.closeEntry();
            }
        }
        return archivePath;
    }

    /**
     * Ensures that the given name ends with {@code .zip} (case-insensitive).
     *
     * @param name file name to check.
     * @return a name that ends with {@code .zip}.
     */
    private static String ensureZipExtension(String name) {
        String n = name.trim();
        return n.toLowerCase().endsWith(".zip") ? n : n + ".zip";
    }

    /**
     * If {@code path} already exists, generates a unique sibling path by appending
     * {@code " (n)"} before the extension, incrementing {@code n} until a free name is found.
     *
     * @param path desired target path.
     * @return the same path if it does not exist; otherwise a unique variant.
     */
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

    /**
     * Computes a common parent directory for the given paths. This is used to
     * produce relative entry names inside the ZIP so that directory structure is preserved.
     *
     * @param paths collection of absolute, normalized file paths.
     * @return the common parent directory or {@code null} if not applicable.
     */
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

    /**
     * Builds a ZIP entry name for {@code file} relative to {@code base}. Falls back
     * to {@code file.getFileName()} if relativization fails or base is {@code null}.
     * Path separators are normalized to {@code '/'} for ZIP compatibility.
     *
     * @param base common root (may be {@code null}).
     * @param file absolute file path.
     * @return normalized entry name using forward slashes.
     */
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

    /**
     * Ensures the entry name is unique within the archive.
     * If {@code candidate} already exists in {@code used}, appends {@code " (n)"} before
     * the file extension within the same directory until a unique name is produced.
     *
     * @param candidate desired entry name (may be blank or {@code null}).
     * @param used      set of names that are already taken; will be updated.
     * @return a unique entry name.
     */
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
