package com.akshit.auth.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class LoginRequest {

    @NotNull(message = "Email is required")
    @Length(min = 1, message = "Email cannot be empty")
    private String email;

    @NotNull(message = "Password is required")
    @Length(min = 1, message = "Password cannot be empty")
    private String password;
}