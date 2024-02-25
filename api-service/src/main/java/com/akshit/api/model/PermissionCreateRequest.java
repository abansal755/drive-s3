package com.akshit.api.model;

import com.akshit.api.entity.PermissionType;
import com.akshit.api.entity.ResourceType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class PermissionCreateRequest {

    @NotNull(message = "Permission type is required")
    private PermissionType permissionType;

    @NotNull(message = "Resource type is required")
    private ResourceType resourceType;

    @NotNull(message = "Resource ID is required")
    private Long resourceId;

    @NotNull(message = "User ID is required")
    private Long userId;
}
