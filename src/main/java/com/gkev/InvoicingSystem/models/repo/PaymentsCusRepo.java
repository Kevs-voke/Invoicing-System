package com.gkev.InvoicingSystem.models.repo;

import com.gkev.InvoicingSystem.models.DTO.PaymentCustResDTO;
import com.gkev.InvoicingSystem.models.DTO.PaymentsFilterDTO;
import reactor.core.publisher.Flux;

public interface PaymentsCusRepo {
    Flux<PaymentCustResDTO> getPayments(PaymentsFilterDTO filter, int page, int size);
}