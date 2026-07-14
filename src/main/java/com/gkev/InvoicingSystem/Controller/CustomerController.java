package com.gkev.InvoicingSystem.Controller;

import com.gkev.InvoicingSystem.Service.CustomerService;
import com.gkev.InvoicingSystem.Service.UserService;
import com.gkev.InvoicingSystem.models.DTO.CusFilterDTO;
import com.gkev.InvoicingSystem.models.DTO.CusRegDTO;
import com.gkev.InvoicingSystem.models.DTO.CustDashboardStatsDTO;
import com.gkev.InvoicingSystem.models.DTO.CustomerInvoiceResDTO;
import com.gkev.InvoicingSystem.models.DTO.CustomerDetailResDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final UserService userService;
    private final CustomerService customerService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('MANAGER')")
    public Mono<ResponseEntity<String>> registerNewCustomer(@Valid @RequestBody CusRegDTO cusRegDTO) {
        return userService.createCustomer(cusRegDTO)
                .map(user -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(user)
                );

    }
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
