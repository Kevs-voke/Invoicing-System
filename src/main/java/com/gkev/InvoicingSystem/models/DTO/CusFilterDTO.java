package com.gkev.InvoicingSystem.models.DTO;

import com.gkev.InvoicingSystem.Exceptions.InvoicingException;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;

public record CusFilterDTO(
        String email,
        String phoneNumber,
        Long customerNo
    ) {


    public boolean hasEmail() {
        return email != null && !email.isBlank();
    }

    public boolean hasPhoneNumber() {
        return phoneNumber != null && !phoneNumber.isBlank();
    }

    public boolean hasCustomerNo() {
        return customerNo != null;
    }


}

