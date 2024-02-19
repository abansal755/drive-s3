package com.akshit.auth.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;

import java.util.Arrays;

import static com.akshit.auth.config.AppConfig.ACCESS_EXPIRE_AFTER_MILLIS;
import static com.akshit.auth.config.AppConfig.REFRESH_EXPIRE_AFTER_MILLIS;

public class Cookies {

    public static String readServletCookie(HttpServletRequest request, String name){
        Cookie[] cookies = request.getCookies();
        if(cookies == null) return null;
        return Arrays.stream(cookies)
                .filter(cookie -> name.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findAny()
                .orElse(null);
    }

    public static ResponseCookie getAccessTokenCookie(String token){
        return ResponseCookie
                .from("access_token", token)
                .path("/")
                .maxAge(ACCESS_EXPIRE_AFTER_MILLIS /1000)
                .build();
    }

    public static ResponseCookie getRefreshTokenCookie(String token){
        return ResponseCookie
                .from("refresh_token", token)
                .path("/")
                .maxAge(REFRESH_EXPIRE_AFTER_MILLIS/1000)
                .build();
    }
}
