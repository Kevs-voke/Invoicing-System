package com.gkev.InvoicingSystem.models.DTO;

import lombok.Builder;

@Builder
public record CustomerDetailResDTO(
        Long userNo,
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) {}