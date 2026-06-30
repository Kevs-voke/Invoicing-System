package com.gkev.InvoicingSystem.models.DTO;

import com.gkev.InvoicingSystem.Exceptions.InvoicingException;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;

public record CusFilterDTO(
        String email,
        String phoneNumber,
        Long customerNo,

        Long invoiceNo,
        String status,
        LocalDate dueDateFrom,
        LocalDate dueDateTo,


        Sort.Direction dueDateSortDirection
) {

    public CusFilterDTO {
        if (dueDateSortDirection == null) {
            dueDateSortDirection = Sort.Direction.ASC;
        }
        if (dueDateFrom != null && dueDateTo != null && dueDateFrom.isAfter(dueDateTo)) {
            throw new InvoicingException("INVALID_DATE_RANGE", "dueDateFrom cannot be after dueDateTo");
        }
    }


    public boolean hasStatus() {
        return status != null;
    }

    public boolean hasEmail() {
        return email != null && !email.isBlank();
    }

    public boolean hasPhoneNumber() {
        return phoneNumber != null && !phoneNumber.isBlank();
    }

    public boolean hasCustomerNo() {
        return customerNo != null;
    }

    public boolean hasInvoiceNo() {
        return invoiceNo != null;
    }

    public boolean hasDueDateFrom() {
        return dueDateFrom != null;
    }

    public boolean hasDueDateTo() {
        return dueDateTo != null;
    }


}

