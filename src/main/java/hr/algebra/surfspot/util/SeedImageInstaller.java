package hr.algebra.surfspot.util;

import hr.algebra.surfspot.exception.ConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class SeedImageInstaller {

    private static final String SEED_IMAGES_DIR = "/seed-images/";

    private SeedImageInstaller() {
    }

    public static void installSeedImages(List<String> filenames) {
        Path storageDir = ImageStorage.getStorageDir();

        for (String filename : filenames) {
            Path target = storageDir.resolve(filename);
            if (Files.exists(target)) {
                continue;
            }

            try (InputStream in = SeedImageInstaller.class.getResourceAsStream(SEED_IMAGES_DIR + filename)) {
                if (in == null) {
                    throw new ConfigurationException("Seed image not found in resources: " + filename);
                }
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new ConfigurationException("Failed to install seed image: " + filename, e);
            }
        }
    }
}