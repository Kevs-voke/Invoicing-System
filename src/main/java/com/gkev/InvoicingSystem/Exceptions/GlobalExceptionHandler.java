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


        private Mono<ResponseEntity<ErrorResponseDTO>> buildResponse(String errorCode, String message, HttpStatus status) {
            logger.error("Exception occurred. code={}, message={}", errorCode, message);
            return Mono.just(ResponseEntity
                    .status(status)
                    .body(new ErrorResponseDTO(errorCode, message)));
        }

        @ExceptionHandler(UserException.class)
        public Mono<ResponseEntity<ErrorResponseDTO>> handleUserException(UserException e) {
            return buildResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(ResourceNotFound.class)
        public Mono<ResponseEntity<ErrorResponseDTO>> handleResourceNotFound(ResourceNotFound e) {
            return buildResponse(e.getErrorCode(), e.getMessage(), HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(InvoiceCreationException.class)
        public Mono<ResponseEntity<ErrorResponseDTO>> handleInvoiceCreationException(InvoiceCreationException e) {
           return buildResponse(e.getErrorCode(), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ExceptionHandler(InvalidTransitionException.class)
        public Mono<ResponseEntity<ErrorResponseDTO>> handleInvalidTransitionException(InvalidTransitionException e) {
            return buildResponse(e.getErrorCode(), e.getMessage(), HttpStatus.CONFLICT);
        }

    }
