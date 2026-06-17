package com.gkev.InvoicingSystem.Exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserException extends RuntimeException {

    private final String errorCode;

    public UserException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}