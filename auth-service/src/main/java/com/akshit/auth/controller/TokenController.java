package com.akshit.auth.controller;

import com.akshit.auth.entity.UserEntity;
import com.akshit.auth.model.AccessTokenSummary;
import com.akshit.auth.model.Token;
import com.akshit.auth.service.JwtService;
import com.akshit.auth.service.TokenService;
import com.akshit.auth.service.UserService;
import com.akshit.auth.utils.Cookies;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.akshit.auth.utils.Cookies.getAccessTokenCookie;

@RestController
@RequestMapping("/api/v1/token")
public class TokenController {

    @Autowired
    private TokenService tokenService;

    @PostMapping("")
    public ResponseEntity<AccessTokenSummary> getNewAccessToken(HttpServletRequest request) {
        return tokenService.getNewAccessToken(request);
    }
}
