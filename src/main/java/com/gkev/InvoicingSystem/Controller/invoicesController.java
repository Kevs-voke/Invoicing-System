package com.gkev.InvoicingSystem.Controller;

import com.gkev.InvoicingSystem.Service.InvoiceService;
import com.gkev.InvoicingSystem.models.DTO.InvoiceCustResDTO;
import com.gkev.InvoicingSystem.models.DTO.InvoiceDTO;
import com.gkev.InvoicingSystem.models.DTO.InvoiceRespDTO;
import com.gkev.InvoicingSystem.models.DTO.InvoicesFilterDTO;
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
            @RequestParam(defaultValue = "10") int size){
    {
        return invoiceService.
                getInvoices(filter,page,size)
                .collectList()
                .map(ResponseEntity::ok);
    }
}
}
