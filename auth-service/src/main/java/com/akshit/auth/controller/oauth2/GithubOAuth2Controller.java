package com.akshit.auth.controller.oauth2;

import com.akshit.auth.entity.UserEntity;
import com.akshit.auth.model.GithubAccessTokenRequestResponse;
import com.akshit.auth.model.GithubGetUserRequestResponse;
import com.akshit.auth.model.Token;
import com.akshit.auth.service.JwtService;
import com.akshit.auth.service.UserService;
import com.akshit.auth.service.oauth2.GithubOAuth2Service;
import com.akshit.auth.utils.Cookies;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/login/oauth2/github")
public class GithubOAuth2Controller {

    @Autowired
    private GithubOAuth2Service githubOAuth2Service;

    @GetMapping("")
    public ResponseEntity<Void> authorizationEndpoint(HttpServletRequest request){
        return githubOAuth2Service.authorizationEndpointHandler(request);
    }

    @GetMapping("/callback")
    public ResponseEntity<Void> callbackEndpoint(
            @RequestParam(required = false) String code,
            @RequestParam String state,
            @RequestParam(required = false) String error,
            HttpServletRequest request
    )
    {
        return githubOAuth2Service.callbackEndpointHandler(code, state, error, request);
    }
}
