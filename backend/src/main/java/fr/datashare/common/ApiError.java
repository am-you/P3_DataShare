package fr.datashare.common;

import java.time.Instant;

// Am.Yo : format d'erreur commun du contrat OpenAPI
public record ApiError(int status, String error, String message, Instant timestamp) {

    public static ApiError of(int status, String error, String message) {
        return new ApiError(status, error, message, Instant.now());
    }
}
