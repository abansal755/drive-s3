package com.akshit.auth.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class RegisterRequest {

    @NotNull(message = "Email is required")
    @Length(min = 1, message = "Email cannot be empty")
    @Email(message = "Incorrect email provided")
    private String email;

    @NotNull(message = "Password is required")
    @Length(min = 5, message = "Password must be at least 5 characters long")
    private String password;

    @NotNull(message = "Confirm Password is required")
    @Length(min = 5, message = "Confirm Password must be at least 5 characters long")
    private String confirmPassword;

    @NotNull(message = "First Name is required")
    @Length(min = 1, message = "First Name cannot by empty")
    private String firstName;

    @NotNull(message = "Last Name is required")
    private String lastName;
}
