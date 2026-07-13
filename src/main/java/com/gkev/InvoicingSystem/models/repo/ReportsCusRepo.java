package com.gkev.InvoicingSystem.models.repo;

import com.gkev.InvoicingSystem.models.DTO.PaymentMethodBreakdownDTO;
import com.gkev.InvoicingSystem.models.DTO.ReportsFilterDTO;
import com.gkev.InvoicingSystem.models.DTO.ReportsSummaryDTO;
import com.gkev.InvoicingSystem.models.DTO.RevenuePointDTO;
import com.gkev.InvoicingSystem.models.DTO.TopCustomerDTO;
import com.gkev.InvoicingSystem.models.DTO.OverdueSummaryDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReportsCusRepo {
    Mono<ReportsSummaryDTO> getSummary(ReportsFilterDTO filter);
    Mono<OverdueSummaryDTO> getOverdueSummary();
    Flux<RevenuePointDTO> getRevenueSeries(ReportsFilterDTO filter);
    Flux<PaymentMethodBreakdownDTO> getPaymentsByMethod(ReportsFilterDTO filter);
    Flux<TopCustomerDTO> getTopCustomers(ReportsFilterDTO filter, int limit);
}