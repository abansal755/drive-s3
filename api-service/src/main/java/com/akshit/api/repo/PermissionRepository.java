package com.akshit.api.repo;

import com.akshit.api.entity.PermissionEntity;
import com.akshit.api.entity.PermissionType;
import com.akshit.api.entity.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {
    public PermissionEntity findByResourceIdAndResourceTypeAndUserId(Long resourceId, ResourceType resourceType, Long userId);
    public void deleteAllByResourceIdAndResourceType(Long resourceId, ResourceType resourceType);
    public PermissionEntity findPermissionEntityById(Long permissionId);
    public List<PermissionEntity> findAllByResourceIdAndResourceType(Long resourceId, ResourceType resourceType);
    public void deleteAllByResourceIdAndResourceTypeAndPermissionTypeAndUserId(Long resourceId, ResourceType resourceType, PermissionType permissionType, Long userId);
    public List<PermissionEntity> findAllByUserId(Long userId);
}
