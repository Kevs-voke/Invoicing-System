package com.gkev.InvoicingSystem.Service;

import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class MonthlySummaryJob implements Job {

    private final InvoiceService invoiceService;
    private final Logger logger = Logger.getLogger(MonthlySummaryJob.class.getName());
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

    }
}
