package com.gkev.InvoicingSystem.models.DTO;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record RevenuePointDTO(
    LocalDate bucketDate,
    BigDecimal amount
){}