package com.gkev.InvoicingSystem.models.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InvoiceSummaryReportDb(
        String reportPeriod,
        LocalDate generatedOn,
        BigDecimal totalRevenue,
        BigDecimal outstandingTotal,
        BigDecimal currentTotal,
        int currentCount,
        int overdueCount,
        String topCustomerRecords

) {
}
