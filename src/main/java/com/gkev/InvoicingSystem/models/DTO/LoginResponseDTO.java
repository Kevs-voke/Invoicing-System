package com.gkev.InvoicingSystem.models.DTO;

import java.util.List;

public record LoginResponseDTO(
        String firstName,
        List<String> roles,
        String jwtToken,
        Boolean mustChangePassword
) {
}
