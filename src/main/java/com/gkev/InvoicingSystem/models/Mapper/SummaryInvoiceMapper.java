package com.gkev.InvoicingSystem.models.Mapper;

import com.gkev.InvoicingSystem.models.DTO.InvoiceSummaryReport;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
@Component
public class SummaryInvoiceMapper {

    public Mono<Context> setData(InvoiceSummaryReport report) {
        Context context = new Context();
        Map<String, Object> map = new HashMap<>();
        map.put("reportPeriod", report.reportPeriod());
        map.put("generatedOn", report.generatedOn());
        map.put("outstandingTotal", report.outstandingTotal());
        map.put("currentTotal", report.currentTotal());
        map.put("currentCount", report.currentCount());
        map.put("overdueCount", report.overdueCount());
        map.put("topCustomer", report.topCustomerRecords());
        context.setVariables(map);
        return Mono.just(context);
    }
}
