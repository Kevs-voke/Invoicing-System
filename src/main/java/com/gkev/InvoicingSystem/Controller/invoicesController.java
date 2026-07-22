package com.gkev.InvoicingSystem.Controller;

import com.gkev.InvoicingSystem.Service.InvoiceService;
import com.gkev.InvoicingSystem.models.DTO.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


import java.util.List;

@Controller
@RequestMapping("/invoices")
@RequiredArgsConstructor
@Slf4j

public class invoicesController {
    private final InvoiceService invoiceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public Mono<ResponseEntity<InvoiceRespDTO>> createInvoice(@Valid @RequestBody InvoiceDTO invoiceDTO){
        return invoiceService.createInvoice(invoiceDTO)
                .map(ResponseEntity::ok);
    }
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public Mono<ResponseEntity<List<InvoiceCustResDTO>>> getInvoices(
            InvoicesFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        {
            return invoiceService.
                    getInvoices(filter, page, size)
                    .collectList()
                    .map(ResponseEntity::ok);
        }
    }
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public Mono<ResponseEntity<InvoiceDashboardStatsDTO>>  getInvoiceDashboardStats(){
        return invoiceService.getInvoiceDashboardStats()
                .map(ResponseEntity::ok);
    }
    @GetMapping("/detailed-invoice")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public Mono<ResponseEntity<DetailedInvoiceResDTO>> getDetailedInvoice(
            @RequestParam long customerNo,
            @RequestParam long invoiceNo) {
        return invoiceService.getDetailedInvoice(invoiceNo,customerNo)
                .map(ResponseEntity::ok);
    }
    @GetMapping("/mine")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Mono<ResponseEntity<List<InvoiceCustResDTO>>> getMyInvoices(
            InvoicesFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return invoiceService.getMyInvoices(filter, page, size)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/mine/{invoiceNo}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Mono<ResponseEntity<DetailedInvoiceResDTO>> getMyDetailedInvoice(@PathVariable long invoiceNo) {
        return invoiceService.getMyDetailedInvoice(invoiceNo)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/mine/dashboard")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Mono<ResponseEntity<CustomerDashboardStatsDTO>> getMyDashboardStats() {
        return invoiceService.getMyDashboardStats()
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{invoiceNo}/send-invoice-confirmation")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public Mono<ResponseEntity<Void>> sendInvoiceConfirmation(
            @PathVariable long invoiceNo
    ){
            log.info("PATCH /send-invoice-confirmation called for invoice {}", invoiceNo);

        return invoiceService.notifyCustomerInvoiceCreated(invoiceNo)
                .thenReturn(ResponseEntity.noContent().build());
    }
}