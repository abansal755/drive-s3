package com.akshit.api.repo;

import com.akshit.api.entity.UserRootFolderMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRootFolderMappingRepository extends JpaRepository<UserRootFolderMappingEntity, Long> {
    public UserRootFolderMappingEntity findByFolderId(Long id);
    public UserRootFolderMappingEntity findByUserId(Long id);
}
