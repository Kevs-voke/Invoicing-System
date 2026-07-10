package com.gkev.InvoicingSystem.Exceptions;

public class PdfGenerationException extends RuntimeException {

    private final String errorCode;

    public PdfGenerationException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}