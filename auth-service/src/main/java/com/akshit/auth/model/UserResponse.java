package com.akshit.auth.model;

import com.akshit.auth.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private boolean usernamePasswordRegistration;
    private boolean githubRegistration;
    private long accessTokenExpireAtMillis;

    public static UserResponse.UserResponseBuilder builderFromEntity(UserEntity user){
        return UserResponse
                .builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .usernamePasswordRegistration(user.isUsernamePasswordRegistration())
                .githubRegistration(user.isGithubRegistration());
    }

    public static UserResponse fromEntityAndAccessToken(UserEntity user, Token accessToken){
        return builderFromEntity(user)
                .accessTokenExpireAtMillis(accessToken.getExpireAtMillis())
                .build();
    }

    public static UserResponse fromEntity(UserEntity user){
        return builderFromEntity(user)
                .build();
    }
}
