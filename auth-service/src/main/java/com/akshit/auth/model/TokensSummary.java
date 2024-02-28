package com.akshit.auth.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokensSummary {
    long accessTokenExpireAtMillis;
    long refreshTokenExpireAtMillis;

    public static TokensSummary fromTokens(Token accessToken, Token refreshToken){
        return TokensSummary
                .builder()
                .accessTokenExpireAtMillis(accessToken.getExpireAtMillis())
                .refreshTokenExpireAtMillis(refreshToken.getExpireAtMillis())
                .build();
    }
}
