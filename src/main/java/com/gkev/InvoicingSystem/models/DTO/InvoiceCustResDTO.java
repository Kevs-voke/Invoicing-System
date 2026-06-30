package com.gkev.InvoicingSystem.models.DTO;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record InvoiceCustResDTO (
        String firstName,
        String lastName,
        Long customerNo,
        Long invoiceNo,
        String status,
        LocalDate dueDate,
        LocalDate createdAt,
        BigDecimal amountPaid,
        BigDecimal invoiceTotal


){
}
