package fr.datashare.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// Am.Yo : corps de POST /api/auth/login (US04)
public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {
}
