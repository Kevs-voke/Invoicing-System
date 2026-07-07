package com.gkev.InvoicingSystem.Exceptions;

public class JobSchedulingException extends RuntimeException{


    private final String errorCode;

    public JobSchedulingException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

}
