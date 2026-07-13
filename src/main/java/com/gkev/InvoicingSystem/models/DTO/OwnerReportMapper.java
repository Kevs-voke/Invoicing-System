package com.gkev.InvoicingSystem.models.DTO;

import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;

@Component
public class OwnerReportMapper {


    public Mono<Context> setData(InvoiceSummaryReport report) {
        Context context = new Context();
        context.setVariable("reportPeriod",report.reportPeriod());
        context.setVariable("generatedOn", report.generatedOn());
        return Mono.just(context);
    }
}
