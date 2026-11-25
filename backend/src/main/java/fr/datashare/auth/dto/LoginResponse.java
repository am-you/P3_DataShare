package fr.datashare.auth.dto;

// Am.Yo : JWT renvoyé à la connexion
public record LoginResponse(String token, long expiresIn) {
}
