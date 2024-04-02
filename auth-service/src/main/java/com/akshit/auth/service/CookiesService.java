package com.akshit.auth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static com.akshit.auth.config.AppConfig.ACCESS_EXPIRE_AFTER_MILLIS;
import static com.akshit.auth.config.AppConfig.REFRESH_EXPIRE_AFTER_MILLIS;

@Service
public class CookiesService {

    @Value("${cookie.domain}")
    private String domain;

    public String readServletCookie(HttpServletRequest request, String name){
        Cookie[] cookies = request.getCookies();
        if(cookies == null) return null;
        return Arrays.stream(cookies)
                .filter(cookie -> name.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findAny()
                .orElse(null);
    }

    public ResponseCookie getAccessTokenCookie(String token){
        return getBuilder("access_token", token, (long)ACCESS_EXPIRE_AFTER_MILLIS /1000).build();
    }

    public ResponseCookie getRefreshTokenCookie(String token){
        return getBuilder("refresh_token", token, (long)REFRESH_EXPIRE_AFTER_MILLIS/1000).build();
    }

    public ResponseCookie getLogoutAccessTokenCookie(){
        return getBuilder("access_token", "", 0L).build();
    }

    public ResponseCookie getLogoutRefreshTokenCookie(){
        return getBuilder("refresh_token", "", 0L).build();
    }

    private ResponseCookie.ResponseCookieBuilder getBuilder(String key, String value, Long maxAge){
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie
                .from(key, value)
                .path("/")
                .maxAge(maxAge)
                .httpOnly(true)
                .sameSite("Lax");
        if(domain != null)
            builder = builder.domain(domain);
        if(maxAge != null)
            builder = builder.maxAge(maxAge);
        return builder;
    }
}
