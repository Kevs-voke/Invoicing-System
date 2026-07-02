package com.gkev.InvoicingSystem.models.DTO;

import java.math.BigDecimal;

public record InvoiceItemsResDTO(
       String itemName,
       BigDecimal unitPrice,
       BigDecimal quantity,
       BigDecimal tax,
       BigDecimal tax_total,
       BigDecimal total
) {
}


