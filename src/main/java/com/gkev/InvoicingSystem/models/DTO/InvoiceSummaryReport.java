package com.gkev.InvoicingSystem.models.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InvoiceSummaryReport(
        String reportPeriod,
        LocalDate generatedOn,
        BigDecimal totalRevenue,
        BigDecimal outstandingTotal,
        BigDecimal currentTotal,
        long currentCount,
        long overdueCount,
        List<TopCustomerRecords> topCustomerRecords

) {

    }