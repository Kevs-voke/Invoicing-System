package com.gkev.InvoicingSystem.models.DTO;

import lombok.Builder;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record CustomerInvoiceResDTO(
        String userNo,
        String email,
        String phoneNumber,
        Long invoiceNo,
        String status,
        BigDecimal total,
        LocalDate dueDate
) {
}
