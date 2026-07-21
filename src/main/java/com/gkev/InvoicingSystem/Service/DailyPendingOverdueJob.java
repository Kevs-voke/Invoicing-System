package com.gkev.InvoicingSystem.Service;

import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class DailyPendingOverdueJob implements Job {
    private final Logger logger = LoggerFactory.getLogger(DailyPendingOverdueJob.class);
    private final InvoiceService invoiceService;
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("Updating invoices Status from pending to Overdue job has started");
    try {
        invoiceService.updateStatusPendingOverdue()
   .then()
                .doOnSuccess(unused -> logger.info(" Updating invoices Status from pending to Overdue job successful run "))
                .block(Duration.ofSeconds(30));
    } catch (Exception e) {
        throw new JobExecutionException("Failed toUpdating invoices Status from pending to Overdue ", e);
    }
    }
}
