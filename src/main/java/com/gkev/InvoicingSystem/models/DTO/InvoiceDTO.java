package com.gkev.InvoicingSystem.models.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record InvoiceDTO(
        @NotNull(message = "Customer number is required")
        Long customerNo,

        @NotNull(message = "Due date is required")
        LocalDate dueDate,

        @NotNull(message = "Items list is required")
        @Size(min = 1, message = "Invoice must have at least one item")
        List<InvoiceItemDTO> items
) {}
