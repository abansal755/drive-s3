package com.akshit.api.controller;

import com.akshit.api.exception.ApiException;
import com.akshit.api.model.PermissionCreateRequest;
import com.akshit.api.model.PermissionModifyRequest;
import com.akshit.api.model.PermissionResponse;
import com.akshit.api.model.User;
import com.akshit.api.service.PermissionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @PostMapping("")
    public void createPermission(
            @Valid @RequestBody PermissionCreateRequest permissionCreateRequest,
            @AuthenticationPrincipal User user)
    {
        permissionService.createPermission(permissionCreateRequest, user);
    }

    @DeleteMapping("{permissionId}")
    public void deletePermission(
            @PathVariable Long permissionId,
            @AuthenticationPrincipal User user)
    {
        permissionService.deletePermission(permissionId, user);
    }

    @PatchMapping("{permissionId}")
    public void modifyPermission(
            @PathVariable Long permissionId,
            @Valid @RequestBody PermissionModifyRequest permissionModifyRequest,
            @AuthenticationPrincipal User user)
    {
        permissionService.modifyPermission(permissionId, permissionModifyRequest, user);
    }

    @GetMapping
    public List<PermissionResponse> getPermissionsGrantedToUser(@AuthenticationPrincipal User user){
        return permissionService.getPermissionsGrantedToUser(user);
    }
}
