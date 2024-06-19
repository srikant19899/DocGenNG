package com.docGenSvc.exception;

public class DocumentProcessingException extends RuntimeException{
    public DocumentProcessingException(String message){
        super(message);
    }
}
