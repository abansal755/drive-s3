package com.akshit.auth.service;

import com.akshit.auth.entity.UserEntity;
import com.akshit.auth.model.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import static com.akshit.auth.config.AppConfig.ACCESS_EXPIRE_AFTER_MILLIS;
import static com.akshit.auth.config.AppConfig.REFRESH_EXPIRE_AFTER_MILLIS;

@Service
public class JwtService {
    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

//    Generating long-lived access token for admin
//    @Autowired
//    private UserService userService;
//
//    @PostConstruct
//    public void init(){
//        Token token = generateUserToken(userService.findUserById(2L), 1000L * 60 * 60 * 24 * 365 * 10);
//        System.out.println(token.getValue());
//    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Token generateAccessToken(UserEntity user) {
        return generateUserToken(user, ACCESS_EXPIRE_AFTER_MILLIS);
    }

    public Token generateRefreshToken(UserEntity user) {
        return generateUserToken(user, REFRESH_EXPIRE_AFTER_MILLIS);
    }

    private Token generateUserToken(UserEntity user, long expireAfterMillis){
        return generateUserToken(new HashMap<String, Object>(), user, expireAfterMillis);
    }

    private Token generateUserToken(Map<String, Object> extraClaims, UserEntity user, long expireAfterMillis){
        long issuedAtMillis = new Date().getTime();
        long expireAtMillis = issuedAtMillis + expireAfterMillis;

        String token = Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(issuedAtMillis))
                .setExpiration(new Date(expireAtMillis))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
        return Token
                .builder()
                .value(token)
                .expireAtMillis(expireAtMillis)
                .build();
    }

    public String generateToken(String subject, Map<String, Object> extraClaims){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver){
        return resolver.apply(extractAllClaims(token));
    }

    public String extractEmail(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token){
        Date expiredAt = extractExpiration(token);
        return expiredAt.before(new Date(System.currentTimeMillis()));
    }
}
