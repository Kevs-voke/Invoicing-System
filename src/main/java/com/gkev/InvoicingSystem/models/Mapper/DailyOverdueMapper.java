package com.gkev.InvoicingSystem.models.Mapper;

import com.gkev.InvoicingSystem.models.DTO.OverdueInvoiceDTO;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;
@Component
public class DailyOverdueMapper {

    public Mono<Context> setData(OverdueInvoiceDTO overdueInvoice) {
        Context context = new Context();
        context.setVariable("invoice", overdueInvoice);
        return Mono.just(context);
    }
}
