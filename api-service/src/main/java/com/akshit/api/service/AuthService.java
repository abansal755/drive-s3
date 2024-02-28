package com.akshit.api.service;

import com.akshit.api.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AuthService {

    @Value("${auth-service.uri}")
    private String authServiceUri;

    @Value("${auth-service.admin-access-token}")
    private String adminAccessToken;

    public User getUserByAccessToken(String accessToken){
        ResponseCookie accessTokenCookie = ResponseCookie
                .from("access_token", accessToken)
                .build();

        return RestClient
                .create()
                .get()
                .uri(authServiceUri + "/api/v1/users")
                .header(HttpHeaders.COOKIE, accessTokenCookie.toString())
                .retrieve()
                .body(User.class);
    }

    public User getUserById(long userId){
        ResponseCookie accessTokenCookie = ResponseCookie
                .from("access_token", adminAccessToken)
                .build();

        return RestClient
                .create()
                .get()
                .uri(authServiceUri + "/api/v1/users/" + String.valueOf(userId))
                .header(HttpHeaders.COOKIE, accessTokenCookie.toString())
                .retrieve()
                .body(User.class);
    }
}
