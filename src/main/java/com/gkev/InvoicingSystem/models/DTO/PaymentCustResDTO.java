package com.gkev.InvoicingSystem.models.DTO;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record PaymentCustResDTO(
    Long paymentNo,
    UUID id,
    String firstName,
    String lastName,
    Long customerNo,
    Long invoiceNo,
    BigDecimal amount,
    String paymentMethod,
    String transactionRef,
    String status,
    LocalDateTime paymentAt
){
    
}