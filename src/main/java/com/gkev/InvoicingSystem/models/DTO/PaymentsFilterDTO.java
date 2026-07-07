package com.gkev.InvoicingSystem.models.DTO;

import com.gkev.InvoicingSystem.Exceptions.InvoicingException;
import com.gkev.InvoicingSystem.models.Enums.PaymentSortBy;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;

public record PaymentsFilterDTO(
    Long customerNo,
    Long invoiceNo,
    String firstName,
    String lastName,
    String paymentMethod,
    String status,
    LocalDate paymentDateFrom,
    LocalDate paymentDateTo,


    PaymentSortBy sortBy,
    Sort.Direction sortDirection
){
    public PaymentsFilterDTO{
        if (sortDirection == null) {
            sortDirection = Sort.Direction.DESC;
        }
        if (sortBy == null) {
            sortBy = PaymentSortBy.PAYMENT_DATE;
        }
        if (paymentDateFrom != null && paymentDateTo != null && paymentDateFrom.isAfter(paymentDateTo)) {
            throw new InvoicingException("INVALID_DATE_RANGE", "Payment date from cannot be after payment date to");
        }
    }

    public boolean hasCustomerNo() {
        return customerNo != null;
    }
    public boolean hasInvoiceNo() {
        return invoiceNo != null;
    }
    public boolean hasFirstName() {
        return firstName != null && !firstName.isBlank();
    }
    public boolean hasLastName() {
        return lastName != null && !lastName.isBlank();
    }
    public boolean hasPaymentMethod() {
        return paymentMethod != null && !paymentMethod.isBlank();
    }
    public boolean hasStatus() {
        return status != null && !status.isBlank();
    }
    public boolean hasPaymentDateFrom() {
        return paymentDateFrom != null;
    }
    public boolean hasPaymentDateTo() {
        return paymentDateTo != null;
    }
}