package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.models.DTO.InvoiceRespDTO;
import com.gkev.InvoicingSystem.models.DTO.MeDTO;
import com.gkev.InvoicingSystem.models.DTO.OverdueInvoiceDTO;
import reactor.core.publisher.Mono;

public interface InvoiceReminderLogger {

    Mono<Void> log(OverdueInvoiceDTO invoice, String channel);
}
