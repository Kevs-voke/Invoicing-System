package com.gkev.InvoicingSystem.models.DTO;

import jakarta.validation.constraints.*;

import java.util.List;

public record UserRegDTO(
        CusRegDTO cusRegDTO,

        @NotNull(message = "Roles list cannot be null")
        @Size(min = 1, message = "At least one role must be assigned")
        List<@NotBlank String> roles



) {
}
