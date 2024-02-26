package com.akshit.api.service;

import com.akshit.api.entity.*;
import com.akshit.api.exception.ApiException;
import com.akshit.api.model.PermissionCreateRequest;
import com.akshit.api.model.User;
import com.akshit.api.repo.FileRepository;
import com.akshit.api.repo.FolderRepository;
import com.akshit.api.repo.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PermissionService {

    @Autowired
    private FolderService folderService;

    @Autowired
    private FileService fileService;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private FileRepository fileRepository;

    private void resourcePermissionNotExistsValidation(PermissionType permissionType, PermissionType existingPermission) throws ApiException {
        if((permissionType == PermissionType.READ) && (existingPermission != null))
            throw new ApiException("This user already has read permission to this resource", HttpStatus.BAD_REQUEST);
        if((permissionType == PermissionType.WRITE) && (existingPermission == PermissionType.WRITE))
            throw new ApiException("This user already has write permission to this resource", HttpStatus.BAD_REQUEST);
    }

    private void permissionExistenceRequiredValidation(PermissionEntity permission) throws ApiException {
        if(permission == null)
            throw new ApiException("Permission not found", HttpStatus.NOT_FOUND);
    }

    public void createPermission(PermissionCreateRequest permissionCreateRequest, User user) throws ApiException {
        PermissionType permissionType = permissionCreateRequest.getPermissionType();
        ResourceType resourceType = permissionCreateRequest.getResourceType();
        Long resourceId = permissionCreateRequest.getResourceId();
        Long userId = permissionCreateRequest.getUserId(); //TODO: validation for user ID
        User otherUser = new User(userId);

        if(resourceType == ResourceType.FOLDER){
            FolderEntity folder = folderRepository.findFolderEntityById(resourceId);
            folderService.folderExistenceRequiredValidation(folder);

            if(!folderService.checkIfFolderIsOwnedByUser(folder, user))
                throw new ApiException("Not allowed to give permission for this folder", HttpStatus.FORBIDDEN);

            PermissionType existingPermission = folderService.getFolderPermissionForUser(folder, otherUser);
            resourcePermissionNotExistsValidation(permissionType, existingPermission);
        }
        else if(resourceType == ResourceType.FILE){
            FileEntity file = fileRepository.findFileEntityById(resourceId);
            fileService.fileExistenceRequiredValidation(file);

            if(!fileService.checkIfFileIsOwnedByUser(file, user))
                throw new ApiException("Not allowed to give permission for this file", HttpStatus.FORBIDDEN);

            PermissionType existingPermission = fileService.getFilePermissionForUser(file, otherUser);
            resourcePermissionNotExistsValidation(permissionType, existingPermission);
        }
        permissionRepository.save(PermissionEntity
                .builder()
                .userId(userId)
                .permissionType(permissionType)
                .resourceType(resourceType)
                .resourceId(resourceId)
                .createdAt(new Date().getTime())
                .build());
    }

    public void deletePermission(Long permissionId, User user) throws ApiException {
        PermissionEntity permission = permissionRepository.findPermissionEntityById(permissionId);
        permissionExistenceRequiredValidation(permission);

        ResourceType resourceType = permission.getResourceType();
        Long resourceId = permission.getResourceId();
        boolean isOwner = false;
        if(resourceType == ResourceType.FOLDER){
            FolderEntity folder = folderRepository.findFolderEntityById(resourceId);
            isOwner = folderService.checkIfFolderIsOwnedByUser(folder, user);
        }
        else if(resourceType == ResourceType.FILE){
            FileEntity file = fileRepository.findFileEntityById(resourceId);
            isOwner = fileService.checkIfFileIsOwnedByUser(file, user);
        }
        if(!isOwner)
            throw new ApiException("User is not allowed to delete this permission", HttpStatus.FORBIDDEN);

        permissionRepository.deleteById(permissionId);
    }
}
