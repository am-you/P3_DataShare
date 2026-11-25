package fr.datashare.download.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

// Am.Yo : infos affichées avant le téléchargement
public record DownloadInfo(
        String name,
        long sizeBytes,
        String mimeType,
        Instant expiresAt,
        @JsonProperty("protected") boolean protectedFile
) {
}
