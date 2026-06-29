package com.gkev.InvoicingSystem.Controller;

import com.gkev.InvoicingSystem.Service.InvoiceService;
import com.gkev.InvoicingSystem.models.DTO.InvoiceDTO;
import com.gkev.InvoicingSystem.models.DTO.InvoiceRespDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

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
}
