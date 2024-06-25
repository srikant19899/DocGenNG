package com.docGenSvc.exception;


import com.docGenSvc.model.response.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


import java.io.IOException;
import java.util.Arrays;

@ControllerAdvice
public class DocGenNGExceptionHandler {
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<Object> handleInvalidInputExceptionHandler(InvalidInputException e) {
        return exceptionResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        return exceptionResponse("500.00.1000", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(QuoteXException.class)
    public ResponseEntity<Object> quoteXExceptionHandler(QuoteXException e) {
        return exceptionResponse(e.getErrorCode(), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DocumentProcessingException.class)
    public ResponseEntity<Object> documentProcessingExceptionHandler(DocumentProcessingException e) {
        return exceptionResponse(e.getErrorCode(), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DocGenNGException.class) // docGenNGExcp -> e, msg, errorcode
    public ResponseEntity<Object> docGenNGExceptionHandler(DocGenNGException e) {// fixed here and pass
        return exceptionResponse(e.getErrorCode(), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Object> exceptionResponse(String errorCode, String message, HttpStatusCode code) {
        JobSubmitResponse errorResponse = new JobSubmitResponse(
                Arrays.asList(new Errors(errorCode, message)),
                ""
        );
        return new ResponseEntity<>(errorResponse, code);
    }
}
