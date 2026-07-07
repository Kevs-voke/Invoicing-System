package com.gkev.InvoicingSystem.models.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record OverdueInvoiceDTO(
        String firstName,
        String lastName,
        Long userNo,
        String email,
        String phoneNumber,
        Long invoiceNo,
        LocalDateTime createdAt,
        LocalDate dueDate,
        BigDecimal total,
        BigDecimal amountPaid,
        BigDecimal overdue
) {
}
