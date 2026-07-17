package com.gkev.InvoicingSystem.models.DTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AdminUserListItemDTO(
        UUID id,
        Long userNo,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        List<String> roles,
        Boolean disabled,
        LocalDateTime createdAt
) {
}
