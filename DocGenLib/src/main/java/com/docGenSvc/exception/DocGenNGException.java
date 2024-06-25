package com.docGenSvc.exception;

import lombok.Getter;

@Getter
public class DocGenNGException extends RuntimeException {
    private final String errorCode;

    public DocGenNGException(Throwable cause, String message, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public DocGenNGException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}
