package com.akshit.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> apiException(ApiException exception){
        String message = "Something went wrong";
        if(exception.getMessage() != null)
            message = exception.getMessage();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            if(exception.getHttpStatus() != null)
                status = exception.getHttpStatus();
        return ResponseEntity
                .status(status)
                .body(ExceptionResponse
                        .builder()
                        .status(status)
                        .message(message)
                        .build());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse exception(Exception exception){
        return ExceptionResponse
                .builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Something went wrong")
                .build();
    }
}
