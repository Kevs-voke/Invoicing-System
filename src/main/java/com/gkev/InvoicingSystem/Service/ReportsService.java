package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.models.DTO.PaymentMethodBreakdownDTO;
import com.gkev.InvoicingSystem.models.DTO.ReportsFilterDTO;
import com.gkev.InvoicingSystem.models.DTO.ReportsSummaryDTO;
import com.gkev.InvoicingSystem.models.DTO.OverdueSummaryDTO;
import com.gkev.InvoicingSystem.models.DTO.RevenuePointDTO;
import com.gkev.InvoicingSystem.models.DTO.TopCustomerDTO;
import com.gkev.InvoicingSystem.models.repo.ReportsCusRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReportsService {
    private static final Logger logger = LoggerFactory.getLogger(ReportsService.class);
    private final ReportsCusRepo reportsCusRepo;

    public Mono<ReportsSummaryDTO> getSummary(ReportsFilterDTO filter) {
        logger.info("Fetching reports summary");
        return reportsCusRepo.getSummary(filter);
    }

    public Flux<RevenuePointDTO> getRevenueSeries(ReportsFilterDTO filter) {
        logger.info("Fetching revenue series");
        return reportsCusRepo.getRevenueSeries(filter);
    }

    public Flux<PaymentMethodBreakdownDTO> getPaymentsByMethod(ReportsFilterDTO filter) {
        logger.info("Fetching payments by method");
        return reportsCusRepo.getPaymentsByMethod(filter);
    }

    public Flux<TopCustomerDTO> getTopCustomers(ReportsFilterDTO filter, int limit) {
        logger.info("Fetching top customers");
        return reportsCusRepo.getTopCustomers(filter, limit);
    }
    public Mono<OverdueSummaryDTO> getOverdueSummary() {
    logger.info("Fetching overdue summary");
    return reportsCusRepo.getOverdueSummary();
}
}