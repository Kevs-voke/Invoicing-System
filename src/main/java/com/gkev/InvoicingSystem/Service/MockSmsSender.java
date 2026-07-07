package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.models.DTO.OverdueInvoiceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MockSmsSender implements InvoiceCustomerReminder {

    private final InvoiceReminderLogger logger;
    @Override
    public Mono<Void> send(OverdueInvoiceDTO invoice) {
        return logger.log(invoice, "CONSOLE");

    }
}
