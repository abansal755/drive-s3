package com.akshit.auth.service;

import com.akshit.auth.entity.UserEntity;
import com.akshit.auth.exception.ApiException;
import com.akshit.auth.model.AccessTokenSummary;
import com.akshit.auth.model.Token;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private CookiesService cookiesService;

    public ResponseEntity<AccessTokenSummary> getNewAccessToken(HttpServletRequest request) {
        String refreshToken = cookiesService.readServletCookie(request, "refresh_token");
        String email = jwtService.extractEmail(refreshToken);
        if(email == null || jwtService.isTokenExpired(refreshToken))
            throw new ApiException("Invalid refresh token", HttpStatus.BAD_REQUEST);

        UserEntity user = userService.findUserByEmail(email);
        Token accessToken = jwtService.generateAccessToken(user);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookiesService.getAccessTokenCookie(accessToken.getValue()).toString())
                .body(AccessTokenSummary.fromAccessToken(accessToken));
    }
}
