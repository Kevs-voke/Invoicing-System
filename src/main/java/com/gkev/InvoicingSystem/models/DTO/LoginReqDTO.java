package com.gkev.InvoicingSystem.models.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginReqDTO(
        @Email @NotBlank String email,
        @NotBlank String password
) {
}
