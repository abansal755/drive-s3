package com.akshit.auth.controller;

import com.akshit.auth.config.AppConfig;
import com.akshit.auth.entity.UserEntity;
import com.akshit.auth.model.*;
import com.akshit.auth.service.JwtService;
import com.akshit.auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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

import java.util.List;

import static com.akshit.auth.config.AppConfig.ACCESS_EXPIRE_AFTER_MILLIS;
import static com.akshit.auth.config.AppConfig.REFRESH_EXPIRE_AFTER_MILLIS;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public UserResponse getLoggedInUserDetails(HttpServletRequest request, @AuthenticationPrincipal UserEntity user){
        return userService.getLoggedInUserDetails(request, user);
    }

    @GetMapping("{userId}")
    public UserResponse getUserDetails(@PathVariable Long userId){
        return userService.getUserDetails(userId);
    }

    @PostMapping("")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest){
        return userService.registerUser(registerRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest){
        return userService.loginUser(loginRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutUser(){
        return userService.logoutUser();
    }

    @GetMapping("search")
    public List<UserResponse> usersSearch(@RequestParam String v){
        return userService.usersSearch(v);
    }
}
