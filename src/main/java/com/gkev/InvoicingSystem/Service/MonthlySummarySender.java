package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.models.DTO.InvoiceSummaryReport;
import reactor.core.publisher.Mono;

public interface MonthlySummarySender {
    Mono<Void> send(InvoiceSummaryReport report);
}
