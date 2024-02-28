package com.akshit.auth.controller;

import com.akshit.auth.entity.UserEntity;
import com.akshit.auth.model.Token;
import com.akshit.auth.model.TokensSummary;
import com.akshit.auth.service.JwtService;
import com.akshit.auth.service.UserService;
import com.akshit.auth.utils.Cookies;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.akshit.auth.config.AppConfig.ACCESS_EXPIRE_AFTER_MILLIS;
import static com.akshit.auth.utils.Cookies.getAccessTokenCookie;

@RestController
@RequestMapping("/api/v1/token")
public class TokenController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @PostMapping("")
    public ResponseEntity<TokensSummary> getNewAccessToken(HttpServletRequest request) throws Exception {
        String refreshToken = Cookies.readServletCookie(request, "refresh_token");
        String email = jwtService.extractEmail(refreshToken);
        if(email == null || jwtService.isTokenExpired(refreshToken))
            throw new Exception("Invalid refresh token");

        UserEntity user = userService.findUserByEmail(email);
        Token accessToken = jwtService.generateAccessToken(user);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, getAccessTokenCookie(accessToken.getValue()).toString())
                .body(TokensSummary
                        .builder()
                        .accessTokenExpireAtMillis(accessToken.getExpireAtMillis())
                        .refreshTokenExpireAtMillis(jwtService.extractExpiration(refreshToken).getTime())
                        .build());
    }
}
