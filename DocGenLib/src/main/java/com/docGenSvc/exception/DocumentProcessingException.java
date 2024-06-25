package com.docGenSvc.exception;

import lombok.Getter;

@Getter
public class DocumentProcessingException extends RuntimeException{
    private final String errorCode;
    public DocumentProcessingException(Throwable cause,String message, String errorCode) {
        super(message,cause);
        this.errorCode = errorCode;
    }

    public DocumentProcessingException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
