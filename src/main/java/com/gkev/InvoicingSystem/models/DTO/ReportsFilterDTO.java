package com.gkev.InvoicingSystem.models.DTO;

import com.gkev.InvoicingSystem.models.Enums.ReportGranularity;
import java.time.LocalDate;

public record ReportsFilterDTO(
        LocalDate from,
        LocalDate to,
        ReportGranularity granularity
) {
    public ReportsFilterDTO {
        if (to == null) to = LocalDate.now();
        if (from == null) from = to.minusDays(7);
        if (granularity == null) granularity = ReportGranularity.DAILY;
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("'from' date cannot be after 'to' date");
        }
    }
}