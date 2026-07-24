package com.gkev.InvoicingSystem.models.Mapper;

import com.gkev.InvoicingSystem.models.DTO.InvoiceConfirmationResDTO;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;
@Component
public class ConfirmationInvoiceMapper {
    public Mono<Context> setData(InvoiceConfirmationResDTO invoice){
        Context context = new Context();
        context.setVariable("invoiceNo", invoice.invoiceNo());
        context.setVariable("total", invoice.total());
        context.setVariable("totalTax", invoice.totalTax());
        context.setVariable("dueDate", invoice.dueDate());
        context.setVariable("items",invoice.invoiceItems());
        context.setVariable("customerName", invoice.customerName());
        context.setVariable("customerEmail", invoice.email());
        return Mono.just(context);
    }
}