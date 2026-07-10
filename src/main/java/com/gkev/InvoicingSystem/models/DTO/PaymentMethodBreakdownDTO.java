package com.gkev.InvoicingSystem.models.DTO;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record PaymentMethodBreakdownDTO(
    String method,
    BigDecimal amount,
    BigDecimal percentage
) {}