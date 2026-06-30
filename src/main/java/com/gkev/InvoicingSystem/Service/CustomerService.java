package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.Exceptions.ResourceNotFound;
import com.gkev.InvoicingSystem.models.DTO.CusFilterDTO;
import com.gkev.InvoicingSystem.models.DTO.CustDashboardStatsDTO;
import com.gkev.InvoicingSystem.models.DTO.CustomerInvoiceResDTO;
import com.gkev.InvoicingSystem.models.repo.CustomerRepo;
import com.gkev.InvoicingSystem.models.repo.UsersRepo;
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
    private final UsersRepo usersRepo;


    public Flux<CustomerInvoiceResDTO> findCustomers(CusFilterDTO filter, int page, int size) {
        logger.info("querying customers records");
        return customerRepo.findCustomers(filter, page, size )
                .switchIfEmpty(Mono.error(() -> new ResourceNotFound("NOT_FOUND", " Customers records could not be found")))
                .doOnComplete(() -> logger.info("customers records found  "));
    }

    public Mono<CustDashboardStatsDTO> getCustDashboardStats( ){
        logger.info("querying Customer Dashboard Stats");
        return usersRepo
                .getCustomerDashboardStats()
                .switchIfEmpty(Mono.error(() -> new ResourceNotFound("NOT_FOUND", "Dashboard Stats could not be found"))
                )
                .doOnSuccess(response -> logger.info("Dashboard Stats records found "));

    }

}
