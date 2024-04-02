package com.akshit.auth.service;

import com.akshit.auth.entity.Role;
import com.akshit.auth.entity.UserEntity;
import com.akshit.auth.exception.ApiException;
import com.akshit.auth.model.*;
import com.akshit.auth.repo.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CookiesService cookiesService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = userRepository.findUserEntityByEmail(username);
        if(user == null)
            throw new UsernameNotFoundException("User not found");
        return user;
    }

    public UserEntity findUserByEmail(String email){
        return userRepository.findUserEntityByEmail(email);
    }

    public UserEntity findUserById(Long userId){
        return userRepository.findUserEntityById(userId);
    }

    public UserEntity save(UserEntity user){
        return userRepository.save(user);
    }

    public UserResponse getLoggedInUserDetails(HttpServletRequest request, UserEntity user){
        String accessTokenValue = cookiesService.readServletCookie(request, "access_token");
        return UserResponse
                .builderFromEntity(user)
                .accessTokenExpireAtMillis(jwtService.extractExpiration(accessTokenValue).getTime())
                .build();
    }

    public UserResponse getUserDetails(Long userId){
        UserEntity user = findUserById(userId);
        return UserResponse.fromEntity(user);
    }

    public ResponseEntity<UserResponse> registerUser(RegisterRequest registerRequest){
        if(!registerRequest.getPassword().equals(registerRequest.getConfirmPassword()))
            throw new ApiException("Passwords don't match", HttpStatus.BAD_REQUEST);

        UserEntity user = userRepository.save(UserEntity
                .builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .usernamePasswordRegistration(true)
                .githubRegistration(false)
                .role(Role.USER)
                .build());

        Token accessToken = jwtService.generateAccessToken(user);
        Token refreshToken = jwtService.generateRefreshToken(user);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookiesService.getAccessTokenCookie(accessToken.getValue()).toString())
                .header(HttpHeaders.SET_COOKIE, cookiesService.getRefreshTokenCookie(refreshToken.getValue()).toString())
                .body(UserResponse.fromEntityAndAccessToken(user, accessToken));
    }

    public ResponseEntity<UserResponse> loginUser(LoginRequest loginRequest){
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(
                loginRequest.getEmail(),
                loginRequest.getPassword());
        authenticationManager.authenticate(authentication);

        UserEntity user = findUserByEmail(loginRequest.getEmail());
        Token accessToken = jwtService.generateAccessToken(user);
        Token refreshToken = jwtService.generateRefreshToken(user);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookiesService.getAccessTokenCookie(accessToken.getValue()).toString())
                .header(HttpHeaders.SET_COOKIE, cookiesService.getRefreshTokenCookie(refreshToken.getValue()).toString())
                .body(UserResponse.fromEntityAndAccessToken(user, accessToken));
    }

    public ResponseEntity<Void> logoutUser(){
        ResponseCookie removeAccessTokenCookie = cookiesService.getLogoutAccessTokenCookie();
        ResponseCookie removeRefreshTokenCookie = cookiesService.getLogoutRefreshTokenCookie();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, removeAccessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, removeRefreshTokenCookie.toString())
                .build();
    }

    public List<UserResponse> usersSearch(String value){
        String search = "%" + value + "%";
        return userRepository.searchUsers(search)
                .stream()
                .map(UserResponse::fromEntity)
                .toList();
    }
}
