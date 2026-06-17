package com.gkev.InvoicingSystem.Exceptions;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class UserException extends RuntimeException {
   private final String ErrorCode;
   private final static Logger logger = LoggerFactory.getLogger(UserException.class);
   public UserException(String ErrorCode, String message ) {
        super(message);
        this.ErrorCode = ErrorCode;
        logger.error(message );


    }
}
