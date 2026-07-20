package com.gkev.InvoicingSystem.models.DTO;

import lombok.Builder;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record CustomerInvoiceResDTO(
        String userNo,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        Long invoiceNo,
        String status,
        BigDecimal total,
        LocalDate dueDate
//        total should be dues or balance .
) {
}
