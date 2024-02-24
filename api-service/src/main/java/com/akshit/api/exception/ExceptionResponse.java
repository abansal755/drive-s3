package com.akshit.api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class ExceptionResponse {
    private HttpStatus status;
    private String message;
    private StackTraceElement[] stackTrace;
}
