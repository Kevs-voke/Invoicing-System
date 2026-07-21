package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.models.DTO.OverdueInvoiceDTO;
import com.gkev.InvoicingSystem.models.Enums.Channel;
import reactor.core.publisher.Mono;

public interface DailyOverdueChannel {

    Mono<Void> send(OverdueInvoiceDTO invoice);
    Channel channel();
}
