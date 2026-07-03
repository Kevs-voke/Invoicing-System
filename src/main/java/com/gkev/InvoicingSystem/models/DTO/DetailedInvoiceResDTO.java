package com.gkev.InvoicingSystem.models.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DetailedInvoiceResDTO(
        Long invoiceNo,
        String status,
        LocalDate dueDate,
        BigDecimal total_tax,
        BigDecimal total,
        BigDecimal amount_paid,
        BigDecimal balance,
        List<InvoiceItemsResDTO> items
) {
}
