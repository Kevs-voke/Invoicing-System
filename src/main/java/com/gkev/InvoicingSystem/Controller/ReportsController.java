package com.gkev.InvoicingSystem.Controller;

<<<<<<< Updated upstream
import com.gkev.InvoicingSystem.Service.ReportsService;
import com.gkev.InvoicingSystem.models.DTO.PaymentMethodBreakdownDTO;
import com.gkev.InvoicingSystem.models.DTO.ReportsFilterDTO;
import com.gkev.InvoicingSystem.models.DTO.ReportsSummaryDTO;
import com.gkev.InvoicingSystem.models.DTO.RevenuePointDTO;
import com.gkev.InvoicingSystem.models.DTO.TopCustomerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportsController  {
    private final ReportsService reportsService;

    @GetMapping("/summary")
    public Mono<ResponseEntity<ReportsSummaryDTO>> getSummary(ReportsFilterDTO filter) {
        return reportsService.getSummary(filter)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/revenue")
    public Mono<ResponseEntity<List<RevenuePointDTO>>> getRevenueSeries(ReportsFilterDTO filter) {
        return reportsService.getRevenueSeries(filter)
        .collectList()
        .map(ResponseEntity::ok);
    }

    @GetMapping("/payments-by-method")
    public Mono<ResponseEntity<List<PaymentMethodBreakdownDTO>>> getPaymentsByMethod(ReportsFilterDTO filter) {
        return reportsService.getPaymentsByMethod(filter)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/top-customers")
    public Mono<ResponseEntity<List<TopCustomerDTO>>> getTopCustomers(ReportsFilterDTO filter, 
    @RequestParam(defaultValue = "5") int limit) {
        return reportsService.getTopCustomers(filter, limit)
                .collectList()
                .map(ResponseEntity::ok);
    }
}
=======
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/reports")
public class ReportsController {
    @GetMapping
    public Mono<String> reports() {
        return Mono.just("index.html");
    }
}
>>>>>>> Stashed changes
