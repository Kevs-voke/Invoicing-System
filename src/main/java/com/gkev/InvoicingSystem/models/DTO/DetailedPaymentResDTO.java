package com.gkev.InvoicingSystem.models.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DetailedPaymentResDTO(
        Long paymentNo,
        Long customerNo,
        String firstName,
        String lastName,
        Long invoiceNo,
        String invoiceStatus,
        BigDecimal invoiceTotal,
        BigDecimal invoiceBalance,
        BigDecimal amount,
        String paymentMethod,
        String transactionRef,
        String notes,
        String status,
        LocalDateTime paymentAt
) {}