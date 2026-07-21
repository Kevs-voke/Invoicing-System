package com.gkev.InvoicingSystem.Controller;

import com.gkev.InvoicingSystem.Service.CustomerService;
import com.gkev.InvoicingSystem.models.DTO.CusFilterDTO;
import com.gkev.InvoicingSystem.models.DTO.CustDashboardStatsDTO;
import com.gkev.InvoicingSystem.models.DTO.CustomerInvoiceResDTO;
import com.gkev.InvoicingSystem.models.DTO.CustomerDetailResDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public Mono<ResponseEntity<List<CustomerInvoiceResDTO>>> getCustomers(
            CusFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){

        return customerService.findCustomers(filter, page, size)
                .collectList()
                .map(ResponseEntity::ok);
    }
    @GetMapping("/dashboard")
    public Mono<ResponseEntity<CustDashboardStatsDTO>> getCustomers(){
        return customerService
                .getCustDashboardStats()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{userNo}")
    public Mono<ResponseEntity<CustomerDetailResDTO>> getCustomerDetail(@PathVariable Long userNo) {
    return customerService.getCustomerByUserNo(userNo)
            .map(ResponseEntity::ok);
}
}
