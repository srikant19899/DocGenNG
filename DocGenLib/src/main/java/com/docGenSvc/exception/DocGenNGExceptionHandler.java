package com.docGenSvc.exception;


import com.docGenSvc.model.response.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


import java.io.IOException;
import  java.util.Arrays;

@ControllerAdvice
public class DocGenNGExceptionHandler {
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<Object> handleInvalidInputException(InvalidInputException e) {
        return exceptionResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
       return exceptionResponse(e,HttpStatus.INTERNAL_SERVER_ERROR);

    }
    @ExceptionHandler(QuoteXException.class)
    public ResponseEntity<Object> quateXException(QuoteXException e){
        return exceptionResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Object> ioExceptionHanler(IOException e){
        return exceptionResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    private ResponseEntity<Object> exceptionResponse(Exception e, HttpStatusCode code){
        JobSubmitResponse errorResponse = new JobSubmitResponse(
                Arrays.asList(new Errors(code.toString(), e.getMessage())),
                ""
        );
        return new ResponseEntity<>(errorResponse, code);
    }
}
