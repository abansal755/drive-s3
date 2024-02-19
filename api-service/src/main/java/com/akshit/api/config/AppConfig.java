package com.akshit.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AppConfig {

    @Bean
    S3Client s3Client() {
        return S3Client
                .builder()
                .region(Region.AP_SOUTH_1)
                .build();
    }
}
