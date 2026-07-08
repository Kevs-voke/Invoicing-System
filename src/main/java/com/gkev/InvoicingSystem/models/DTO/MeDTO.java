package com.gkev.InvoicingSystem.models.DTO;
import java.util.List;

public record MeDTO(
       String firstName,
       String  lastName,
       String phoneNumber,
       String email,
       Long userNumber,
       List<String> roles
) {
}
