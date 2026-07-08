package com.gkev.InvoicingSystem.models.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DetailedPaymentResDTO(
        Long paymentNo,
        Long customerNo,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        Long invoiceNo,
        String invoiceStatus,
        LocalDateTime invoiceCreatedAt,
        LocalDateTime invoiceDueDate,
        BigDecimal invoiceTotal,
        BigDecimal invoiceBalance,
        BigDecimal amount,
        String paymentMethod,
        String transactionRef,
        String notes,
        String status,
        LocalDateTime paymentAt,
        LocalDateTime createdAt,
        LocalDateTime confirmedAt,
        String confirmedByFirstName,
        String confirmedByLastName,
        LocalDateTime failedAt,
        String failedByFirstName,
        String failedByLastName
) {}