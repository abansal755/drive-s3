package com.akshit.api.config;

import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class AppConfig {

//    @Bean
//    public S3Client s3Client() {
//        return S3Client
//                .builder()
//                .region(Region.AP_SOUTH_1)
//                .build();
//    }
}
