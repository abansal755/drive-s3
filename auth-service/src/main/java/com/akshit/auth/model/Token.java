package com.akshit.auth.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Token {
    private String value;
    private long expireAtMillis;
}
