package com.gkev.InvoicingSystem.models.DTO;

import java.util.List;

public record LoginResApiDTO(
        String firstName,
        List<String> roles,
        Boolean mustChangePassword
) { }
