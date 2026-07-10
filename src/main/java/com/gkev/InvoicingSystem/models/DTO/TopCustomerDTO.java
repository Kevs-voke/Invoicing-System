package com.gkev.InvoicingSystem.models.DTO;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record TopCustomerDTO(
    Long customerNo,
    long invoiceCount,
    BigDecimal paidAmount,
    BigDecimal outstandingAmount,
    LocalDate lastPaymentDate
) {}