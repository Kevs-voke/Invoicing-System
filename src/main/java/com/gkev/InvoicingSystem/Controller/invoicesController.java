package com.gkev.InvoicingSystem.Controller;

import com.gkev.InvoicingSystem.Service.InvoiceService;
import com.gkev.InvoicingSystem.models.DTO.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class invoicesController {
    private final InvoiceService invoiceService;

    @PostMapping
    public Mono<ResponseEntity<InvoiceRespDTO>> createInvoice(@Valid @RequestBody InvoiceDTO invoiceDTO){
        return invoiceService.createInvoice(invoiceDTO)
                .map(ResponseEntity::ok);
    }
    @GetMapping
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
    public Mono<ResponseEntity<InvoiceDashboardStatsDTO>>  getInvoiceDashboardStats(){
        return invoiceService.getInvoiceDashboardStats()
                .map(ResponseEntity::ok);
    }
    @GetMapping("/detailed-invoice")
    public Mono<ResponseEntity<DetailedInvoiceResDTO>> getDetailedInvoice(
            @RequestParam long customerNo,
            @RequestParam long invoiceNo) {
        return invoiceService.getDetailedInvoice(invoiceNo,customerNo)
                .map(ResponseEntity::ok);
    }
    @PatchMapping("/{invoiceNo}/send-invoice-confirmation")
    public Mono<ResponseEntity<Void>> notifyCustomerInvoiceCreated(
            @PathVariable long invoiceNo
    ){
        return invoiceService.notifyCustomerInvoiceCreated(invoiceNo)
                .map(rx -> ResponseEntity.noContent().build());
    }
}
