package com.gkev.InvoicingSystem.models.DTO;

import java.util.List;

public record LoginResponseDTO(
        String email,
        List<String> roles
) {
}
