package com.akshit.auth.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccessTokenSummary {
    private long accessTokenExpireAtMillis;

    public static AccessTokenSummary fromAccessToken(Token accessToken){
        return AccessTokenSummary
                .builder()
                .accessTokenExpireAtMillis(accessToken.getExpireAtMillis())
                .build();
    }
}
