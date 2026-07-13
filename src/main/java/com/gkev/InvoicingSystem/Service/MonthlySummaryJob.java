package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.models.DTO.EmailMessage;
import com.gkev.InvoicingSystem.models.Mapper.OwnerReportMapper;
import com.gkev.InvoicingSystem.models.Mapper.SummaryInvoiceMapper;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class MonthlySummaryJob implements Job {

    private final InvoiceService invoiceService;
    private final SpringTemplateEngine emailTemplateEngine;
    private final PdfGeneratorService pdfGeneratorService;
    private final SummaryInvoiceMapper invoiceMapper;
    private final SummaryReportEmailService summaryReport;
    private final OwnerReportMapper ownerReport;
    private final Logger logger = LoggerFactory.getLogger(MonthlySummaryJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            invoiceService.getInvoiceSummaryReport()
                    .flatMap(report ->
                            invoiceMapper.setData(report)
                                    .flatMap(invoiceContext -> {
                                        String html = emailTemplateEngine.process("summaryInvoiceReport", invoiceContext);
                                        return pdfGeneratorService.invoiceSummaryHtmlToPdf(html)
                                                .flatMap(pdf -> ownerReport.setData(report)
                                                        .map(summaryEmail -> {
                                                            String reportEmailHtml = emailTemplateEngine.process("EmailMonthlySummary", summaryEmail);
                                                            return new EmailMessage(
                                                                    null,
                                                                    "Monthly Report for " + report.reportPeriod(),
                                                                    reportEmailHtml,
                                                                    pdf,
                                                                    report.reportPeriod() + "_Monthly_report.pdf"
                                                            );
                                                        })
                                                        .flatMap(summaryReport::sendEmailToOwners)
                                                );
                                    })
                    )
                    .then()
                    .doOnSuccess(unused -> logger.info("Monthly summary report email sent successfully"))
                    .block(Duration.ofSeconds(30));
        } catch (Exception e) {
            throw new JobExecutionException("Failed to send monthly summary report", e);
        }
    }
}
