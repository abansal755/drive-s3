package com.akshit.auth.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
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

    private List<String> getExceptionStackStrace(Exception exception){
        List<String> stackTrace = Arrays
                .stream(exception.getStackTrace())
                .map(StackTraceElement::toString)
                .toList();
        stackTrace.forEach(System.err::println);
        if(environment.matchesProfiles("production"))
            return null;
        return stackTrace;
    }

    private String getExceptionString(Exception exception){
        System.err.println(exception.toString());
        if(environment.matchesProfiles("production"))
            return null;
        return exception.toString();
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
                        .exception(getExceptionString(exception))
                        .stackTrace(getExceptionStackStrace(exception))
                        .build());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionResponse authenticationException(AuthenticationException authException){
        return ExceptionResponse
                .builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message("Unable to authenticate")
                .exception(getExceptionString(authException))
                .stackTrace(getExceptionStackStrace(authException))
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
                .exception(getExceptionString(exception))
                .stackTrace(getExceptionStackStrace(exception))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse badCredentialsException(BadCredentialsException badCredentialsException){
        return ExceptionResponse
                .builder()
                .status(HttpStatus.BAD_REQUEST)
                .message("Invalid credentials entered")
                .exception(getExceptionString(badCredentialsException))
                .stackTrace(getExceptionStackStrace(badCredentialsException))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse exception(Exception exception){
        return ExceptionResponse
                .builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Something went wrong")
                .exception(getExceptionString(exception))
                .stackTrace(getExceptionStackStrace(exception))
                .build();
    }
}
