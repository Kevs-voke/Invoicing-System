package com.gkev.InvoicingSystem.Service;

import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class DailySchedulerOverdueJob implements Job {
    private final MockSmsSender mockSmsSender;
    private final InvoiceService invoiceService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            invoiceService.getOverdueInvoiceCust()
                    .flatMap(mockSmsSender::send)
                    .then()
                    .block(Duration.ofMillis(4000));
        } catch (Exception e) {
            throw new JobExecutionException("Failed to send overdue invoice reminders", e);
        }
    }

}
