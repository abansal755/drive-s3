package com.akshit.auth.repo;

import com.akshit.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findUserEntityByEmail(String email);
    UserEntity findUserEntityById(Long id);
    List<UserEntity> findTop10ByEmailLikeOrFirstNameLikeOrLastNameLike(String email, String firstName, String lastName);
}
