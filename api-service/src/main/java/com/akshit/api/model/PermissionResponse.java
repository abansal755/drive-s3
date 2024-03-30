package com.akshit.api.model;

import com.akshit.api.entity.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class PermissionResponse {
    private Long id;
    private User user;
    private PermissionType permissionType;
    private Long createdAt;
    private Boolean grantedToAnAncestorFolder;

    private ResourceType resourceType;
    private File file;
    private Folder folder;

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

    public static PermissionResponse fromPermissionEntity(PermissionEntity permission,
                                                          Function<Long, FileEntity> findFileEntityById,
                                                          Function<Long, FolderEntity> findFolderEntityById)
    {
        PermissionResponseBuilder builder = PermissionResponse
                .builder()
                .id(permission.getId())
                .permissionType(permission.getPermissionType())
                .resourceType(permission.getResourceType())
                .createdAt(permission.getCreatedAt());

        ResourceType resourceType = permission.getResourceType();
        Long resourceId = permission.getResourceId();
        if(resourceType == ResourceType.FILE)
            builder = builder.file(File.fromEntity(findFileEntityById.apply(resourceId)));
        else
            builder = builder.folder(Folder.fromEntity(findFolderEntityById.apply(resourceId)));
        return builder.build();
    }
}
