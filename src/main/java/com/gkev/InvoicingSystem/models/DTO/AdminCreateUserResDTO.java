package com.gkev.InvoicingSystem.models.DTO;

public record AdminCreateUserResDTO(
        String firstName,
        String lastName,
        String email,
        String role
) {
}
