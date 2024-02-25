package com.akshit.api.controller;

import com.akshit.api.exception.ApiException;
import com.akshit.api.model.PermissionCreateRequest;
import com.akshit.api.model.User;
import com.akshit.api.service.PermissionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @PostMapping("")
    public void createPermission(
            @Valid @RequestBody PermissionCreateRequest permissionCreateRequest,
            @AuthenticationPrincipal User user) throws ApiException
    {
        permissionService.createPermission(permissionCreateRequest, user);
    }
}
