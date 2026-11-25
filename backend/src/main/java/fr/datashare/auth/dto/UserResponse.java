package fr.datashare.auth.dto;

import java.time.Instant;
import java.util.UUID;

// Am.Yo : compte créé, sans le mot de passe
public record UserResponse(UUID id, String email, Instant createdAt) {
}
