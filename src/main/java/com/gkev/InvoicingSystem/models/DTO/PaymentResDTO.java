package com.gkev.InvoicingSystem.models.DTO;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResDTO(
        Long customerNo,
        Long invoiceNo,
        BigDecimal amount,
        String transactionRef,
        String paymentMethod,
        String notes,
        LocalDateTime paymentAt
) {
}
