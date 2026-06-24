package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.Exceptions.ResourceNotFound;
import com.gkev.InvoicingSystem.models.DTO.CusFilterDTO;
import com.gkev.InvoicingSystem.models.DTO.CustomerInvoiceResDTO;
import com.gkev.InvoicingSystem.models.repo.CustomerRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final Logger logger= LoggerFactory.getLogger(CustomerService.class);
    private final CustomerRepo customerRepo;


    public Flux<CustomerInvoiceResDTO> findCustomers(CusFilterDTO filter, int size, int page) {
        logger.info("querying customers records");
        return customerRepo.findCustomers(filter, size, page )
                .switchIfEmpty(Mono.error(() -> new ResourceNotFound("NOT_FOUND", "Records could not be found")))
                .doOnComplete(() -> logger.info("records found "));
    }

}
