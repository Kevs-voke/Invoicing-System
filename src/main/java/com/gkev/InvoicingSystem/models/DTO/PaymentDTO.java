package com.gkev.InvoicingSystem.models.DTO;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentDTO(
        @NotNull(message = "Customer number is required")
        Long customerNo,
        @NotNull(message = "Invoice number is required")
        Long invoiceNo,

        BigDecimal amount,
        String transaction_ref,
        String payment_method,
        String notes

) {
}
