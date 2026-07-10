package com.gkev.InvoicingSystem.models.DTO;

import java.math.BigDecimal;

public record TopCustomerRecords(
        long customerNo,
        String name,
        int invoiceCount,
        BigDecimal totalValue
) {
}