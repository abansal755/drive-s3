package com.akshit.auth.model;

import com.akshit.auth.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private boolean usernamePasswordRegistration;
    private boolean githubRegistration;

    public static UserResponse fromEntity(UserEntity user){
        return UserResponse
                .builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .usernamePasswordRegistration(user.isUsernamePasswordRegistration())
                .githubRegistration(user.isGithubRegistration())
                .build();
    }
}
