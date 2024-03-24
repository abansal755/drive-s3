package com.akshit.auth.service;

import com.akshit.auth.entity.Role;
import com.akshit.auth.entity.UserEntity;
import com.akshit.auth.model.*;
import com.akshit.auth.repo.UserRepository;
import com.akshit.auth.utils.Cookies;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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

import static com.akshit.auth.utils.Cookies.getAccessTokenCookie;
import static com.akshit.auth.utils.Cookies.getRefreshTokenCookie;

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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserEntityByEmail(username);
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
        String accessTokenValue = Cookies.readServletCookie(request, "access_token");
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
                .header(HttpHeaders.SET_COOKIE, getAccessTokenCookie(accessToken.getValue()).toString())
                .header(HttpHeaders.SET_COOKIE, getRefreshTokenCookie(refreshToken.getValue()).toString())
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
                .header(HttpHeaders.SET_COOKIE, getAccessTokenCookie(accessToken.getValue()).toString())
                .header(HttpHeaders.SET_COOKIE, getRefreshTokenCookie(refreshToken.getValue()).toString())
                .body(UserResponse.fromEntityAndAccessToken(user, accessToken));
    }

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

    public List<UserResponse> usersSearch(String value){
        String search = "%" + value + "%";
        return userRepository.findTop10ByEmailLikeOrFirstNameLikeOrLastNameLike(search, search, search)
                .stream()
                .map(UserResponse::fromEntity)
                .toList();
    }
}
