package com.akshit.auth.config;

import com.akshit.auth.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Autowired
    private UserService userService;

    @Value("${jwt.access-expire-after-millis}")
    private int _ACCESS_EXPIRE_AFTER_MILLIS;

    @Value("${jwt.refresh-expire-after-millis}")
    private int _REFRESH_EXPIRE_AFTER_MILLIS;

    public static int ACCESS_EXPIRE_AFTER_MILLIS;
    public static int REFRESH_EXPIRE_AFTER_MILLIS;

    @PostConstruct
    public void init(){
        ACCESS_EXPIRE_AFTER_MILLIS = _ACCESS_EXPIRE_AFTER_MILLIS;
        REFRESH_EXPIRE_AFTER_MILLIS = _REFRESH_EXPIRE_AFTER_MILLIS;
    }

    @Bean
    public AuthenticationManager authenticationManager(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(userService);
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
