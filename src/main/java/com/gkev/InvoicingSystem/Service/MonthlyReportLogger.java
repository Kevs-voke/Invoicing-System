package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.models.DTO.InvoiceSummaryReport;
import com.gkev.InvoicingSystem.models.DTO.TopCustomerRecords;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class MonthlyReportLogger {
    public Mono<Void> log(InvoiceSummaryReport report, String destination) {

        log.info("""
                
                ================== MONTHLY REPORT [{}] ==================
                Report Period      : {}
                Generated On       : {}
                
                Financial Summary
                -----------------
                Total Revenue      : {}
                Outstanding Total  : {}
                
                Current Invoices
                ----------------
                Count              : {}
                Total              : {}
                
                Overdue Invoices
                ----------------
                Count              : {}
                
                Top Customers
                -------------
                {}
                
                =========================================================
                """,
                destination,
                report.reportPeriod(),
                report.generatedOn(),
                report.totalRevenue(),
                report.outstandingTotal(),
                report.currentCount(),
                report.currentTotal(),
                report.overdueCount(),
                formatCustomers(report.topCustomerRecords())
        );

        return Mono.empty();
    }

    private String formatCustomers(List<TopCustomerRecords> customers) {
        if (customers == null || customers.isEmpty()) {
            return "No top customers found.";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < customers.size(); i++) {
            TopCustomerRecords customer = customers.get(i);

            sb.append(String.format(
                    "%n%d. Customer No : %d" +
                            "%n   Name         : %s" +
                            "%n   Invoices     : %d" +
                            "%n   Total Value  : %s%n",
                    i + 1,
                    customer.customerNo(),
                    customer.name(),
                    customer.invoiceCount(),
                    customer.totalValue()
            ));
        }

        return sb.toString();
    }

}
