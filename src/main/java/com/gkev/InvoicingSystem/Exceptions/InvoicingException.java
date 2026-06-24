package com.gkev.InvoicingSystem.Exceptions;

public class InvoicingException extends RuntimeException {

    private final String errorCode;

    public InvoicingException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }


}
