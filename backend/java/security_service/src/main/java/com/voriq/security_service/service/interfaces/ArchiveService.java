package com.voriq.security_service.service.interfaces;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

public interface ArchiveService {

    Path createArchive(Path outDir, String archiveName, Set<Path> files) throws IOException;

    Path createArchive(Path outDir, String archiveName, Set<Path> files, boolean autoRenameIfExists) throws IOException;

}
