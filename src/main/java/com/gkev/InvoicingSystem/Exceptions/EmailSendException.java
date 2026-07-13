package com.gkev.InvoicingSystem.Exceptions;

public class EmailSendException extends RuntimeException {
    private final String errorCode;
    public EmailSendException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
