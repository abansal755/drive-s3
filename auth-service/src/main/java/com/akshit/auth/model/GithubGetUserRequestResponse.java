package com.akshit.auth.model;

import lombok.Data;

@Data
public class GithubGetUserRequestResponse {
    private String email;
    private String name;
}
