package com.docGenSvc.exception;

import lombok.Getter;

@Getter
public class InvalidInputException extends RuntimeException {
    private final String errorCode;
    public InvalidInputException(Throwable cause,String message, String errorCode) {
        super(message,cause);
        this.errorCode = errorCode;
    }

    public InvalidInputException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
