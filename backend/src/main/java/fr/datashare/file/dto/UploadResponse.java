package fr.datashare.file.dto;

import java.time.Instant;
import java.util.UUID;

// Am.Yo : réponse après téléversement (US01)
public record UploadResponse(
        UUID id,
        String name,
        long sizeBytes,
        String downloadUrl,
        Instant expiresAt
) {
}
