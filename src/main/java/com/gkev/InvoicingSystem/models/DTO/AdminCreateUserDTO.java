package com.gkev.InvoicingSystem.models.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AdminCreateUserDTO(

        @NotBlank(message = "First name is required")
        String firstName,

        String lastName,

        @Email(message = "Email should be valid")
        @NotBlank(message = "Email is required")
        String email,

        @Pattern(regexp = "\\+?[0-9]{7,15}", message = "Phone number must be valid")
        String phoneNumber,

        @NotBlank(message = "Role is required")
        @Pattern(regexp = "MANAGER|STAFF|CUSTOMER", message = "Role must be one of MANAGER, STAFF, CUSTOMER")
        String role

) {
}
