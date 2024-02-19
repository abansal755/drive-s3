package com.akshit.auth.controller;

import com.akshit.auth.config.AppConfig;
import com.akshit.auth.entity.UserEntity;
import com.akshit.auth.model.LoginRequest;
import com.akshit.auth.model.RegisterRequest;
import com.akshit.auth.model.UserResponse;
import com.akshit.auth.service.JwtService;
import com.akshit.auth.service.UserService;
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
    public UserResponse getUserDetails(@AuthenticationPrincipal UserEntity user){
        return UserResponse.fromEntity(user);
    }

    @PostMapping("")
    public ResponseEntity<UserResponse> registerUser(@RequestBody RegisterRequest registerRequest){
        UserEntity user = userService.registerUser(registerRequest);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, getAccessTokenCookie(accessToken).toString())
                .header(HttpHeaders.SET_COOKIE, getRefreshTokenCookie(refreshToken).toString())
                .body(UserResponse.fromEntity(user));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> loginUser(@RequestBody LoginRequest loginRequest){
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(
                loginRequest.getEmail(),
                loginRequest.getPassword());
        authenticationManager.authenticate(authentication);

        UserEntity user = userService.findUserByEmail(loginRequest.getEmail());
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, getAccessTokenCookie(accessToken).toString())
                .header(HttpHeaders.SET_COOKIE, getRefreshTokenCookie(refreshToken).toString())
                .body(UserResponse.fromEntity(user));
    }
}
