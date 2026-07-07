package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.models.DTO.OverdueInvoiceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class InvoiceOverdueLogger implements InvoiceReminderLogger {
    @Override
    public Mono<Void> log(OverdueInvoiceDTO invoice, String channel) {
        log.info("""
                [{}] Reminder sent
                Invoice No : {}
                Customer   : {} {}
                Email       : {}
                Phone       : {}
                Due Date    : {}
                Total       : {}
                Amount Paid : {}
                Balance     : {}
                """,
                channel,
                invoice.invoiceNo(),
                invoice.firstName(),
                invoice.lastName(),
                invoice.email(),
                invoice.phoneNumber(),
                invoice.dueDate(),
                invoice.total(),
                invoice.amountPaid(),
                invoice.overdue());
        return Mono.empty();
    }
}
