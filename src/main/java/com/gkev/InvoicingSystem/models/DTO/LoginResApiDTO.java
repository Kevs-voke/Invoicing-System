package com.gkev.InvoicingSystem.models.DTO;

import java.util.List;

public record LoginResApiDTO(
        String email,
        List<String> roles
) { }
