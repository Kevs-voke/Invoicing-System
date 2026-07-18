package com.gkev.InvoicingSystem.Exceptions;

public class InvalidTransitionException extends RuntimeException {
    private final String errorCode;
    public InvalidTransitionException
            (String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
