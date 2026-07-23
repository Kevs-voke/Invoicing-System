package com.gkev.InvoicingSystem.models.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordDTO(
        @NotBlank String token,
        @NotBlank @Size(min = 8, message = "New password must be at least 8 characters") String newPassword
) {
}
