package com.gkev.InvoicingSystem.models.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoiceConfirmationDTO(
        String customerName,
        String email,
        long invoiceNo,
        BigDecimal totalTax,
        BigDecimal total,
        LocalDate dueDate,
        String invoiceItems
) {
}