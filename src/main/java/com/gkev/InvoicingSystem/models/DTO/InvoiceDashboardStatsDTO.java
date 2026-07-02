package com.gkev.InvoicingSystem.models.DTO;

import java.math.BigDecimal;

public record InvoiceDashboardStatsDTO(
        int draft,
        int pending,
        int overdue,
        BigDecimal amount_overdue,
        BigDecimal amount_receivables
) {
}
