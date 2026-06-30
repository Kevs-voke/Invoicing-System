package com.gkev.InvoicingSystem.models.DTO;

import com.gkev.InvoicingSystem.Exceptions.InvoicingException;
import com.gkev.InvoicingSystem.models.Enums.InvoiceSortBy;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;

public record InvoicesFilterDTO(
        String firstName,
        String lastName,
        Long customerNo,
        Long invoiceNo,
        String status,
        LocalDate dueDateFrom,
        LocalDate dueDateTo,

      InvoiceSortBy sortBy,
        Sort.Direction sortDirection
) {



    public InvoicesFilterDTO{
        if (sortDirection == null) {
            sortDirection = Sort.Direction.ASC;
        }
        if (sortBy == null) {
           sortBy = InvoiceSortBy.DUE_DATE;
        }
        if (dueDateFrom != null && dueDateTo != null && dueDateFrom.isAfter(dueDateTo)) {
            throw new InvoicingException("INVALID_DATE_RANGE", "dueDateFrom cannot be after dueDateTo");
        }
    }
    public boolean hasFirstName() {
        return firstName != null && !firstName.isBlank();
    }
    public boolean hasLastName() {
        return lastName != null && !lastName.isBlank();
    }
    public boolean hasCustomerNo() {
        return customerNo != null;
    }
    public boolean hasStatus() {
        return status != null && !status.isBlank();
    }
    public boolean hasDueDateFrom() {
        return dueDateFrom != null;
    }
    public boolean hasDueDateTo() {
        return dueDateTo != null;
    }
    public boolean hasInvoiceNo() {
        return invoiceNo != null;
    }

}
