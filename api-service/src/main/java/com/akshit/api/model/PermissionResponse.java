package com.akshit.api.model;

import com.akshit.api.entity.PermissionType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PermissionResponse {
    private Long id;
    private User user;
    private PermissionType permissionType;
    private Long createdAt;
    private boolean grantedToAnAncestorFolder;
}
