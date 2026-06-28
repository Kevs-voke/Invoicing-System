package com.gkev.InvoicingSystem.Controller;

import com.gkev.InvoicingSystem.models.DTO.InvoiceDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class invoicesController {

    @PostMapping
    public <InvoiceResDTO> createInvoice(@Valid @RequestBody InvoiceDTO invoiceDTO){
        return
    }
}
