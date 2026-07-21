package com.gkev.InvoicingSystem.models.DTO;

import jakarta.validation.constraints.NotBlank;

public record OneTimeLoginDTO(
        @NotBlank(message = "Token is required")
        String token
){

}
