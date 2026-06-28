package com.gkev.InvoicingSystem.models.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
public record InvoiceItemDTO(
        @NotBlank(message = "Item name is required")
        String itemName,

        @NotNull(message = "Unit price is required")
        @DecimalMin(value = "0.01", message = "Unit price must be greater than zero")
        BigDecimal unitPrice,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity,

        @NotNull(message = "Tax is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Tax cannot be negative")
        BigDecimal tax
) {}
