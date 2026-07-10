package com.gkev.InvoicingSystem.Service;

import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class MonthlySummaryJob implements Job {

    private final InvoiceService invoiceService;
    MonthlySummaryLoggerService monthlySummaryLoggerService;
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            invoiceService.getInvoiceSummaryReport()
                    .flatMap(monthlySummaryLoggerService::send)
                    .then()
                    .block(Duration.ofMillis(4000));
    } catch (Exception e) {
        throw new JobExecutionException("Failed to send overdue invoice reminders", e);
    }
}
}
