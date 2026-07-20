package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.models.DTO.InvoiceConfirmationResDTO;
import com.gkev.InvoicingSystem.models.Enums.Channel;
import reactor.core.publisher.Mono;

public interface InvoiceConfirmationChannel {
    Mono<Void> send(InvoiceConfirmationResDTO invoice);
    Channel channel();

}
