package com.gkev.InvoicingSystem.models.DTO;

import java.math.BigDecimal;

public record CustomerDashboardStatsDTO(
        int totalInvoices,
        BigDecimal totalPaid,
        BigDecimal outstandingBalance,
        int overdueInvoices
) {}