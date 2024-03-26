package com.akshit.api.model;

import com.akshit.api.entity.PermissionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PermissionModifyRequest {

    @NotNull(message = "Permission Type is required")
    private PermissionType permissionType;
}
