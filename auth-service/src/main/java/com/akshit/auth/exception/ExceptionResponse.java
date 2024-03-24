package com.akshit.auth.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;

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
    private String exception;
    private List<String> stackTrace;
}
