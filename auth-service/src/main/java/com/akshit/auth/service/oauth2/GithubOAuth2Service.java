package com.akshit.auth.service.oauth2;

import com.akshit.auth.model.GithubAccessTokenRequestResponse;
import com.akshit.auth.model.GithubGetUserRequestResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GithubOAuth2Service {

    @Value("${oauth2.client.github.client-id}")
    private String githubClientId;

    @Value("${oauth2.client.github.client-secret}")
    private String githubClientSecret;

    public GithubAccessTokenRequestResponse getGithubAccessTokenRequestResponse(String code){
        String uri = UriComponentsBuilder
                .fromHttpUrl("https://github.com/login/oauth/access_token")
                .queryParam("client_id", githubClientId)
                .queryParam("client_secret", githubClientSecret)
                .queryParam("code", code)
                .encode()
                .build()
                .toString();
        return RestClient
                .create()
                .post()
                .uri(uri)
                .header(HttpHeaders.ACCEPT, "application/json")
                .retrieve()
                .body(GithubAccessTokenRequestResponse.class);
    }

    public GithubGetUserRequestResponse getGithubGetUserRequestResponse(String accessToken){
        return RestClient
                .create()
                .get()
                .uri("https://api.github.com/user")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(GithubGetUserRequestResponse.class);
    }
}
