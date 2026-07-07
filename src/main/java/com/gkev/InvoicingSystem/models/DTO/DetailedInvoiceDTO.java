package com.gkev.InvoicingSystem.models.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DetailedInvoiceDTO(
        Long invoiceNo,
        String status,
        LocalDate dueDate,
        BigDecimal totalTax,
        BigDecimal total,
        BigDecimal amountPaid,
        BigDecimal balances,
        String invoiceItems
) {
}
