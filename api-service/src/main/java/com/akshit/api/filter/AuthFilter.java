package com.akshit.api.filter;

import com.akshit.api.model.User;
import com.akshit.api.repo.UserRootFolderMappingRepository;
import com.akshit.api.service.FolderService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class AuthFilter extends OncePerRequestFilter {

    @Value("${auth-service.uri}")
    private String authServiceUri;

    @Autowired
    private FolderService folderService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException
    {
        String accessToken = readServletCookie(request, "access_token");
        if(accessToken == null){
            filterChain.doFilter(request, response);
            return;
        }

        ResponseCookie accessTokenCookie = ResponseCookie
                .from("access_token", accessToken)
                .build();
        User user = RestClient
                .create()
                .get()
                .uri(authServiceUri + "/api/v1/users")
                .header(HttpHeaders.COOKIE, accessTokenCookie.toString())
                .retrieve()
                .body(User.class);

        folderService.createUserRootFolderMappingIfNotExists(user);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        if(securityContext.getAuthentication() == null){
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    List.of(new SimpleGrantedAuthority("USER"))
            );
            securityContext.setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    private static String readServletCookie(HttpServletRequest request, String name){
        Cookie[] cookies = request.getCookies();
        if(cookies == null) return null;
        return Arrays.stream(cookies)
                .filter(cookie -> name.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findAny()
                .orElse(null);
    }
}
