package com.gkev.InvoicingSystem.Exceptions;

public class InvoiceCreationException extends RuntimeException {
    private final String errorCode;

    public InvoiceCreationException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}