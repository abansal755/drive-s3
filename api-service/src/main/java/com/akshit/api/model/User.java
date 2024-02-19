package com.akshit.api.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class User {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private boolean usernamePasswordRegistration;
    private boolean githubRegistration;
}
