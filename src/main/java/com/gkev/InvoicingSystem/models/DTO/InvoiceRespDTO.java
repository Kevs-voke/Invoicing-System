package com.gkev.InvoicingSystem.models.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InvoiceRespDTO(
        Long invoiceNo,
        String status,
        Long customerNo,
        LocalDate createdAt,
        LocalDate dueDate,
        List<InvoiceItemsResDTO> items,
        BigDecimal totalTax,
        BigDecimal total
) {}




