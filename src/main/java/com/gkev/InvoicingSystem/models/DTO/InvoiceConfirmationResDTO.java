package com.gkev.InvoicingSystem.models.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InvoiceConfirmationResDTO(

        String customerName,
        String email,
        long invoiceNo,
        BigDecimal totalTax,
        BigDecimal total,
        LocalDate dueDate,
        List<InvoiceItemsResDTO> invoiceItems
) {
}