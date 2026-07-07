package com.gkev.InvoicingSystem.models.DTO;

import java.math.BigDecimal;

public record PaymentDashboardStatsDTO(
        int pendingCount,
        BigDecimal pendingAmount,
        int confirmedCount,
        BigDecimal confirmedAmount,
        int failedCount,
        BigDecimal failedAmount
) {}