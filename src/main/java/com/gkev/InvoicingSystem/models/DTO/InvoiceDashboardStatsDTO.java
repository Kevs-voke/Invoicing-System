package com.gkev.InvoicingSystem.models.DTO;

import java.math.BigDecimal;

public record InvoiceDashboardStatsDTO(
        int draft,
        int pending,
        int overdue,
        int total_invoices,
        int total_sent,
        int outstanding_invoices,
        BigDecimal amount_overdue,
        BigDecimal amount_receivables
) {
}
