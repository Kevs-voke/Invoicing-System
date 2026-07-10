package com.gkev.InvoicingSystem.models.DTO;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record ReportsSummaryDTO(
        BigDecimal totalRevenue,
        BigDecimal revenueChangePct,
        BigDecimal totalPayments,
        BigDecimal paymentsChangePct,
        long totalInvoices,
        BigDecimal invoicesChangePct,
        BigDecimal outstandingAmount,
        BigDecimal outstandingChangePct
) {}