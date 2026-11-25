package fr.datashare.file;

import fr.datashare.common.BadRequestException;
import fr.datashare.common.PayloadTooLargeException;
import fr.datashare.common.UnsupportedMediaException;
import fr.datashare.file.dto.UploadResponse;
import fr.datashare.storage.StorageService;
import fr.datashare.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

// Am.Yo : dépôt de fichier connecté (US01)
@Service
public class FileService {

    private static final long MAX_BYTES = 1L * 1024 * 1024 * 1024;
    private static final Set<String> FORBIDDEN_EXTENSIONS = Set.of(
            "exe", "bat", "cmd", "sh", "msi", "js", "vbs", "ps1", "jar");

    private final StoredFileRepository storedFileRepository;
    private final StorageService storageService;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();
    private final String frontUrl;

    public FileService(
            StoredFileRepository storedFileRepository,
            StorageService storageService,
            PasswordEncoder passwordEncoder,
            @Value("${datashare.front-url}") String frontUrl) {
        this.storedFileRepository = storedFileRepository;
        this.storageService = storageService;
        this.passwordEncoder = passwordEncoder;
        this.frontUrl = frontUrl.endsWith("/") ? frontUrl.substring(0, frontUrl.length() - 1) : frontUrl;
    }

    @Transactional
    public UploadResponse upload(
            User owner,
            MultipartFile file,
            String password,
            Integer expirationDays) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Fichier obligatoire");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new PayloadTooLargeException("La taille des fichiers est limitée à 1 Go");
        }

        String originalName = file.getOriginalFilename() == null ? "fichier" : file.getOriginalFilename();
        assertAllowedExtension(originalName);

        int days = expirationDays == null ? 7 : expirationDays;
        if (days < 1 || days > 7) {
            throw new BadRequestException("L'expiration doit être entre 1 et 7 jours");
        }
        if (password != null && !password.isBlank() && password.length() < 6) {
            throw new BadRequestException("Le mot de passe du fichier doit faire au moins 6 caractères");
        }

        Instant now = Instant.now();
        String storageKey = randomToken(48);
        String downloadToken = randomToken(32);
        String passwordHash = (password == null || password.isBlank())
                ? null
                : passwordEncoder.encode(password);

        StoredFile stored = new StoredFile(
                UUID.randomUUID(),
                owner.getId(),
                originalName,
                file.getContentType() == null ? "application/octet-stream" : file.getContentType(),
                file.getSize(),
                storageKey,
                downloadToken,
                passwordHash,
                now.plus(days, ChronoUnit.DAYS),
                now);

        storageService.store(storageKey, file);
        storedFileRepository.save(stored);

        return new UploadResponse(
                stored.getId(),
                stored.getOriginalName(),
                stored.getSizeBytes(),
                frontUrl + "/d/" + stored.getDownloadToken(),
                stored.getExpiresAt());
    }

    private void assertAllowedExtension(String originalName) {
        int dot = originalName.lastIndexOf('.');
        if (dot < 0 || dot == originalName.length() - 1) {
            return;
        }
        String ext = originalName.substring(dot + 1).toLowerCase(Locale.ROOT);
        if (FORBIDDEN_EXTENSIONS.contains(ext)) {
            throw new UnsupportedMediaException("Extension interdite : ." + ext);
        }
    }

    // Am.Yo : token URL-safe, longueur fixe
    private String randomToken(int length) {
        byte[] bytes = new byte[length];
        secureRandom.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        return token.length() >= length ? token.substring(0, length) : token;
    }
}
