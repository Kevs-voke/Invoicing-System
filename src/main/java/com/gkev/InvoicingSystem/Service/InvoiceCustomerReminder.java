package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.models.DTO.OverdueInvoiceDTO;
import reactor.core.publisher.Mono;

public interface InvoiceCustomerReminder {
    Mono<Void> send(OverdueInvoiceDTO invoice);
}
