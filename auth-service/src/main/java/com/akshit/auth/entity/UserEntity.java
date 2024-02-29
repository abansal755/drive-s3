package com.akshit.auth.entity;

import com.akshit.auth.model.GithubGetUserRequestResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;
    
    private String password;

    private String firstName;
    private String lastName;

    @Column(nullable = false)
    private boolean usernamePasswordRegistration;

    @Column(nullable = false)
    private boolean githubRegistration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static UserEntity fromGithubGetUserRequestResponse(GithubGetUserRequestResponse githubUser){
        String[] names = splitName(githubUser.getName());
        String firstName = names[0];
        String lastName = names[1];

        return UserEntity
                .builder()
                .email(githubUser.getEmail())
                .firstName(firstName)
                .lastName(lastName)
                .usernamePasswordRegistration(false)
                .githubRegistration(true)
                .role(Role.USER)
                .build();
    }

    private static String[] splitName(String name){
        String firstName, lastName = null;
        int spaceIdx = name.indexOf(" ");
        if(spaceIdx != -1){
            firstName = name.substring(0, spaceIdx);
            lastName = name.substring(spaceIdx + 1);
        }
        else
            firstName = name;
        return new String[]{firstName, lastName};
    }
}
