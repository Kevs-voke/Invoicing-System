package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.models.DTO.InvoiceSummaryReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MonthlySummaryLoggerService implements  MonthlySummarySender{
    private final MonthlyReportLogger reportLogger;
    @Override
    public Mono<Void> send(InvoiceSummaryReport report) {
      return   reportLogger.log(report, "email");
    }
}
