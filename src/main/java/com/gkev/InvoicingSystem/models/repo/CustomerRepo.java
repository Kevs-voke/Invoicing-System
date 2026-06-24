package com.gkev.InvoicingSystem.models.repo;

import com.gkev.InvoicingSystem.models.DTO.CusFilterDTO;
import com.gkev.InvoicingSystem.models.DTO.CustomerInvoiceResDTO;
import reactor.core.publisher.Flux;


public interface CustomerRepo {
    Flux<CustomerInvoiceResDTO> findCustomers(CusFilterDTO filter, int page, int size);
}
