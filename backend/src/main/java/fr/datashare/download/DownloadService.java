package fr.datashare.download;

import fr.datashare.common.GoneException;
import fr.datashare.common.NotFoundException;
import fr.datashare.common.UnauthorizedException;
import fr.datashare.download.dto.DownloadInfo;
import fr.datashare.file.StoredFile;
import fr.datashare.file.StoredFileRepository;
import fr.datashare.storage.StorageService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;

// Am.Yo : lien public de téléchargement (US02)
@Service
public class DownloadService {

    private final StoredFileRepository storedFileRepository;
    private final StorageService storageService;
    private final PasswordEncoder passwordEncoder;

    public DownloadService(
            StoredFileRepository storedFileRepository,
            StorageService storageService,
            PasswordEncoder passwordEncoder) {
        this.storedFileRepository = storedFileRepository;
        this.storageService = storageService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public DownloadInfo getInfo(String token) {
        StoredFile file = requireActiveFile(token);
        return new DownloadInfo(
                file.getOriginalName(),
                file.getSizeBytes(),
                file.getMimeType(),
                file.getExpiresAt(),
                file.isProtected());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Resource> download(String token, String password) {
        StoredFile file = requireActiveFile(token);
        assertPassword(file, password);

        InputStream stream = storageService.open(file.getStorageKey());
        InputStreamResource body = new InputStreamResource(stream);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition(file.getOriginalName()))
                .contentType(MediaType.parseMediaType(file.getMimeType()))
                .contentLength(file.getSizeBytes())
                .body(body);
    }

    private StoredFile requireActiveFile(String token) {
        StoredFile file = storedFileRepository.findByDownloadToken(token)
                .orElseThrow(() -> new NotFoundException("Lien invalide"));
        if (file.isExpired()) {
            throw new GoneException(
                    "Ce fichier n'est plus disponible en téléchargement car il a expiré");
        }
        return file;
    }

    private void assertPassword(StoredFile file, String password) {
        if (!file.isProtected()) {
            return;
        }
        if (password == null || password.isBlank()
                || !passwordEncoder.matches(password, file.getPasswordHash())) {
            throw new UnauthorizedException("Mot de passe manquant ou incorrect");
        }
    }

    private String contentDisposition(String filename) {
        String safe = filename.replace("\"", "");
        return "attachment; filename=\"" + safe + "\"";
    }
}
