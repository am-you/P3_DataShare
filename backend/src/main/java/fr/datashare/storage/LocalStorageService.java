package fr.datashare.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

// Am.Yo : stockage sur disque local sous datashare.storage.path
@Service
public class LocalStorageService implements StorageService {

    private final Path root;

    public LocalStorageService(@Value("${datashare.storage.path}") String storagePath) throws IOException {
        this.root = Path.of(storagePath).toAbsolutePath().normalize();
        Files.createDirectories(this.root);
    }

    @Override
    public void store(String storageKey, MultipartFile file) {
        Path target = resolve(storageKey);
        try {
            file.transferTo(target);
        } catch (IOException ex) {
            throw new UncheckedIOException("Impossible d'enregistrer le fichier", ex);
        }
    }

    @Override
    public InputStream open(String storageKey) {
        try {
            return Files.newInputStream(resolve(storageKey));
        } catch (IOException ex) {
            throw new UncheckedIOException("Impossible de lire le fichier", ex);
        }
    }

    @Override
    public void delete(String storageKey) {
        try {
            Files.deleteIfExists(resolve(storageKey));
        } catch (IOException ex) {
            throw new UncheckedIOException("Impossible de supprimer le fichier", ex);
        }
    }

    private Path resolve(String storageKey) {
        Path resolved = root.resolve(storageKey).normalize();
        if (!resolved.startsWith(root)) {
            throw new IllegalArgumentException("Chemin de stockage invalide");
        }
        return resolved;
    }
}
