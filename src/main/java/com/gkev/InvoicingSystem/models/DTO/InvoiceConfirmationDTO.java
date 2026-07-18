package com.gkev.InvoicingSystem.models.DTO;

import java.math.BigDecimal;

public record InvoiceConfirmationDTO(
        String customerName,
        String email,
        long invoiceNo,
        BigDecimal totalTax,
        BigDecimal total,
        BigDecimal dueDate,
        String invoiceItems
) {
}
