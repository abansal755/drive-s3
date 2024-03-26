package com.akshit.auth.repo;

import com.akshit.auth.entity.Role;
import com.akshit.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findUserEntityByEmail(String email);
    UserEntity findUserEntityById(Long id);
    @Query(value =
            "SELECT * FROM user_entity " +
            "WHERE role=\"USER\" AND " +
            "(first_name LIKE :search OR last_name LIKE :search OR email LIKE :search) " +
            "LIMIT 10", nativeQuery = true)
    List<UserEntity> searchUsers(@Param("search") String search);
}
