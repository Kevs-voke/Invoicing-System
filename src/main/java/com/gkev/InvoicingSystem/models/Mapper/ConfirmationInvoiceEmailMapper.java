package com.gkev.InvoicingSystem.models.Mapper;

import com.gkev.InvoicingSystem.models.DTO.InvoiceConfirmationResDTO;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;

public class ConfirmationInvoiceEmailMapper {
    public Mono<Context> setData(InvoiceConfirmationResDTO invoice) {
        Context context = new Context();
        context.setVariable("customerNamer", invoice.customerName());
        context.setVariable("invoiceNo", invoice.invoiceNo());
        context.setVariable("totalAmount", invoice.total());
        context.setVariable("dueDate", invoice.dueDate());

        return Mono.just(context);
    }
}
