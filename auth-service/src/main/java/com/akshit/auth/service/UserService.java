package com.akshit.auth.service;

import com.akshit.auth.entity.UserEntity;
import com.akshit.auth.model.RegisterRequest;
import com.akshit.auth.repo.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserEntityByEmail(username);
    }

    public UserEntity findUserByEmail(String email){
        return userRepository.findUserEntityByEmail(email);
    }

    public UserEntity registerUser(RegisterRequest registerRequest){
        UserEntity user = UserEntity
                .builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .usernamePasswordRegistration(true)
                .githubRegistration(false)
                .build();
        return userRepository.save(user);
    }

    public UserEntity save(UserEntity user){
        return userRepository.save(user);
    }
}
