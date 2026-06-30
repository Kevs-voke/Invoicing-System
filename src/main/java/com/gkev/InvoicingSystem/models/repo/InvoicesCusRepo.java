package com.gkev.InvoicingSystem.models.repo;

import com.gkev.InvoicingSystem.models.DTO.InvoiceCustResDTO;
import com.gkev.InvoicingSystem.models.DTO.InvoicesFilterDTO;
import reactor.core.publisher.Flux;

public interface InvoicesCusRepo {
    Flux<InvoiceCustResDTO> getInvoices(InvoicesFilterDTO filter, int page, int size);
}
