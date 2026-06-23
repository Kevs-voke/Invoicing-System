package com.gkev.InvoicingSystem.Exceptions;

import com.gkev.InvoicingSystem.models.DTO.ErrorResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;



@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @ExceptionHandler(UserException.class)
    public Mono<ResponseEntity<ErrorResponseDTO>> handleUserException(UserException e) {

        logger.error("UserException occurred. code={}, message={}",
                e.getErrorCode(),
                e.getMessage()
        );

        ErrorResponseDTO response = new ErrorResponseDTO(
                e.getErrorCode(),
                e.getMessage()

        );


        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response));
    }
}
