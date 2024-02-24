package com.akshit.api.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class ExceptionHandlers {

    @Autowired
    private Environment environment;

    private List<String> getStackTraceListFromException(Exception exception){
        if(environment.matchesProfiles("production"))
            return null;
        return Arrays
                .stream(exception.getStackTrace())
                .map(StackTraceElement::toString)
                .toList();
    }

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
                        .stackTrace(getStackTraceListFromException(exception))
                        .build());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionResponse authenticationException(AuthenticationException authException){
        return ExceptionResponse
                .builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message("Unable to authenticate")
                .stackTrace(getStackTraceListFromException(authException))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse methodArgumentNotValidException(MethodArgumentNotValidException exception){
        return ExceptionResponse
                .builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(
                        exception.getBindingResult().getFieldErrors()
                                .stream()
                                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                .findFirst()
                                .orElse(exception.getMessage()))
                .stackTrace(getStackTraceListFromException(exception))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse exception(Exception exception){
        return ExceptionResponse
                .builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Something went wrong")
                .stackTrace(getStackTraceListFromException(exception))
                .build();
    }
}
