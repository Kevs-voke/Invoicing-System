package com.gkev.InvoicingSystem.models.DTO;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OverdueSummaryDTO(
        BigDecimal overdueAmount
) {
}