package com.akshit.auth.service;

import com.akshit.auth.entity.UserEntity;
import com.akshit.auth.model.AccessTokenSummary;
import com.akshit.auth.model.Token;
import com.akshit.auth.utils.Cookies;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.akshit.auth.utils.Cookies.getAccessTokenCookie;

@Service
public class TokenService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    public ResponseEntity<AccessTokenSummary> getNewAccessToken(HttpServletRequest request) throws Exception {
        String refreshToken = Cookies.readServletCookie(request, "refresh_token");
        String email = jwtService.extractEmail(refreshToken);
        if(email == null || jwtService.isTokenExpired(refreshToken))
            throw new Exception("Invalid refresh token");

        UserEntity user = userService.findUserByEmail(email);
        Token accessToken = jwtService.generateAccessToken(user);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, getAccessTokenCookie(accessToken.getValue()).toString())
                .body(AccessTokenSummary.fromAccessToken(accessToken));
    }
}
