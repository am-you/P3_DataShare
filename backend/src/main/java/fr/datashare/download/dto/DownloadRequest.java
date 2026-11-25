package fr.datashare.download.dto;

// Am.Yo : mot de passe dans le corps, jamais dans l'URL
public record DownloadRequest(String password) {
}
