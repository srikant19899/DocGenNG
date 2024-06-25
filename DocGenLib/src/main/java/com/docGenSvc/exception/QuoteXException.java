package com.docGenSvc.exception;

import lombok.Getter;

@Getter
public class QuoteXException extends RuntimeException {
    private final String errorCode;

    public QuoteXException(Throwable cause, String message, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public QuoteXException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
