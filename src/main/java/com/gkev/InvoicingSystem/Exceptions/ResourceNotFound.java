package com.gkev.InvoicingSystem.Exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ResourceNotFound extends  RuntimeException {
    private final String errorCode;

    public ResourceNotFound(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
