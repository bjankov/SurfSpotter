package hr.algebra.surfspot.util;

import java.io.IOException;
import java.nio.file.*;

public class ImageStorage {
    private static final Path STORAGE_DIR = Paths.get(System.getProperty("user.home"), ".surfspot", "images");

    private ImageStorage() {
    }

    static {
        try {
            Files.createDirectories(STORAGE_DIR);
        } catch (IOException e) {
            throw new RuntimeException("Neuspjelo inicijaliziranje foldera za slike", e);
        }
    }

    public static Path getStorageDir() {
        return STORAGE_DIR;
    }

    public static String saveImage(Path sourcePath) throws IOException {
        String originalName = sourcePath.getFileName().toString();
        String extension = originalName.substring(originalName.lastIndexOf("."));

        String uniqueName = java.util.UUID.randomUUID().toString() + extension;
        Path targetPath = STORAGE_DIR.resolve(uniqueName);

        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        return uniqueName;
    }
}