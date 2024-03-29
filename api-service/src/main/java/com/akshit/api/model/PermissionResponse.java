package com.akshit.api.model;

import com.akshit.api.entity.PermissionEntity;
import com.akshit.api.entity.PermissionType;
import lombok.Builder;
import lombok.Data;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Data
@Builder
public class PermissionResponse {
    private Long id;
    private User user;
    private PermissionType permissionType;
    private Long createdAt;
    private boolean grantedToAnAncestorFolder;

    public static CompletableFuture<PermissionResponse> fromPermissionEntity(PermissionEntity permission,
                                                                             Function<Long, User> getUserById,
                                                                             boolean grantedToAnAncestorFolder)
    {
        return CompletableFuture.supplyAsync(() -> PermissionResponse
                .builder()
                .id(permission.getId())
                .user(getUserById.apply(permission.getUserId()))
                .permissionType(permission.getPermissionType())
                .createdAt(permission.getCreatedAt())
                .grantedToAnAncestorFolder(grantedToAnAncestorFolder)
                .build()
        );
    }
}
