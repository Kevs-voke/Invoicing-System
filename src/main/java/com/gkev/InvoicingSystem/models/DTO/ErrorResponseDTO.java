package com.gkev.InvoicingSystem.models.DTO;

public record ErrorResponseDTO(
        String errorCode,
        String message
) {
}
