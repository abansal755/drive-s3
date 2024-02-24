package com.akshit.api.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlers {

    @Autowired
    private Environment environment;

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
                        .stackTrace(environment.matchesProfiles("production") ? null : exception.getStackTrace())
                        .build());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionResponse authenticationException(AuthenticationException authException){
        return ExceptionResponse
                .builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message("Unable to authenticate")
                .stackTrace(environment.matchesProfiles("production") ? null : authException.getStackTrace())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse exception(Exception exception){
        return ExceptionResponse
                .builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Something went wrong")
                .stackTrace(environment.matchesProfiles("production") ? null : exception.getStackTrace())
                .build();
    }
}
