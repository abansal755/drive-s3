package com.akshit.auth.controller;

import com.akshit.auth.config.AppConfig;
import com.akshit.auth.entity.UserEntity;
import com.akshit.auth.model.*;
import com.akshit.auth.service.JwtService;
import com.akshit.auth.service.UserService;
import com.akshit.auth.utils.Cookies;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.akshit.auth.config.AppConfig.ACCESS_EXPIRE_AFTER_MILLIS;
import static com.akshit.auth.config.AppConfig.REFRESH_EXPIRE_AFTER_MILLIS;
import static com.akshit.auth.utils.Cookies.getAccessTokenCookie;
import static com.akshit.auth.utils.Cookies.getRefreshTokenCookie;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("")
    public UserResponse getLoggedInUserDetails(HttpServletRequest request, @AuthenticationPrincipal UserEntity user){
        String accessTokenValue = Cookies.readServletCookie(request, "access_token");
        return UserResponse
                .builderFromEntity(user)
                .accessTokenExpireAtMillis(jwtService.extractExpiration(accessTokenValue).getTime())
                .build();
    }

    @GetMapping("{userId}")
    public UserResponse getUserDetails(HttpServletRequest request, @PathVariable Long userId){
        UserEntity user = userService.findUserById(userId);
        return UserResponse.fromEntity(user);
    }

    @PostMapping("")
    public ResponseEntity<UserResponse> registerUser(@RequestBody RegisterRequest registerRequest){
        UserEntity user = userService.registerUser(registerRequest);

        Token accessToken = jwtService.generateAccessToken(user);
        Token refreshToken = jwtService.generateRefreshToken(user);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, getAccessTokenCookie(accessToken.getValue()).toString())
                .header(HttpHeaders.SET_COOKIE, getRefreshTokenCookie(refreshToken.getValue()).toString())
                .body(UserResponse.fromEntityAndAccessToken(user, accessToken));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> loginUser(@RequestBody LoginRequest loginRequest){
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(
                loginRequest.getEmail(),
                loginRequest.getPassword());
        authenticationManager.authenticate(authentication);

        UserEntity user = userService.findUserByEmail(loginRequest.getEmail());
        Token accessToken = jwtService.generateAccessToken(user);
        Token refreshToken = jwtService.generateRefreshToken(user);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, getAccessTokenCookie(accessToken.getValue()).toString())
                .header(HttpHeaders.SET_COOKIE, getRefreshTokenCookie(refreshToken.getValue()).toString())
                .body(UserResponse.fromEntityAndAccessToken(user, accessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutUser(){
        ResponseCookie removeAccessTokenCookie = ResponseCookie
                .from("access_token", "")
                .path("/")
                .maxAge(0)
                .build();
        ResponseCookie removeRefreshTokenCookie = ResponseCookie
                .from("refresh_token", "")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, removeAccessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, removeRefreshTokenCookie.toString())
                .build();
    }
}
