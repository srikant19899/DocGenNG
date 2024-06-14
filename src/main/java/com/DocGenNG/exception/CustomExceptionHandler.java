package com.DocGenNG.exception;


import com.DocGenNG.model.response.DocumentsErrorResponse;
import com.DocGenNG.model.response.Errors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import  java.util.Arrays;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<DocumentsErrorResponse> handleInvalidInputException(InvalidInputException e) {
        DocumentsErrorResponse errorResponse = new DocumentsErrorResponse(
                Arrays.asList(new Errors("400", e.getMessage())),
                ""
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DocumentsErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldError().getDefaultMessage();
        DocumentsErrorResponse errorResponse = new DocumentsErrorResponse(
                Arrays.asList(new Errors("400", errorMessage)),
                ""
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DocumentsErrorResponse> handleException(Exception e) {
        DocumentsErrorResponse errorResponse = new DocumentsErrorResponse(
                Arrays.asList(new Errors("500", e.getMessage())),
                ""
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
