package com.akshit.auth.config;

import com.akshit.auth.entity.UserEntity;
import com.akshit.auth.service.CookiesService;
import com.akshit.auth.service.JwtService;
import com.akshit.auth.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Autowired
    private CookiesService cookiesService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    )
    {
        try {
            String accessToken = cookiesService.readServletCookie(request, "access_token");
            if(accessToken == null){
                filterChain.doFilter(request, response);
                return;
            }
            String email = jwtService.extractEmail(accessToken);
            if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserEntity user = userService.findUserByEmail(email);
                if(user != null && !jwtService.isTokenExpired(accessToken)){
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
            filterChain.doFilter(request, response);
        }
        catch (Exception ex){
            resolver.resolveException(request, response, null, ex);
        }
    }
}
