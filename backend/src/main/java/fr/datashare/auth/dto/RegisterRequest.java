package fr.datashare.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Am.Yo : corps de POST /api/auth/register (US03)
public record RegisterRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8) String password
) {
}
