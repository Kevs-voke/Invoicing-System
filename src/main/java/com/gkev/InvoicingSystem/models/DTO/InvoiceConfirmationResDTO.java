package com.gkev.InvoicingSystem.models.DTO;

import java.math.BigDecimal;
import java.util.List;

public record InvoiceConfirmationResDTO(

        String customerName,
        String email,
        long invoiceNo,
        BigDecimal totalTax,
        BigDecimal total,
        BigDecimal dueDate,
        List<InvoiceItemsResDTO> invoiceItems
) {
}
