package com.akshit.api.service;

import com.akshit.api.entity.*;
import com.akshit.api.exception.ApiException;
import com.akshit.api.model.PermissionCreateRequest;
import com.akshit.api.model.PermissionModifyRequest;
import com.akshit.api.model.User;
import com.akshit.api.repo.FileRepository;
import com.akshit.api.repo.FolderRepository;
import com.akshit.api.repo.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private void resourcePermissionNotExistsValidation(PermissionType permissionType, PermissionType existingPermission){
        if((permissionType == PermissionType.READ) && (existingPermission != null))
            throw new ApiException("This user already has read permission to this resource", HttpStatus.BAD_REQUEST);
        if((permissionType == PermissionType.WRITE) && (existingPermission == PermissionType.WRITE))
            throw new ApiException("This user already has write permission to this resource", HttpStatus.BAD_REQUEST);
    }

    private void permissionExistenceRequiredValidation(PermissionEntity permission){
        if(permission == null)
            throw new ApiException("Permission not found", HttpStatus.NOT_FOUND);
    }

    private void permissionModificationAuthorization(PermissionEntity permission, User user){
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
            throw new ApiException("User is not allowed to modify this permission", HttpStatus.FORBIDDEN);
    }

    @Transactional
    public void createPermission(PermissionCreateRequest permissionCreateRequest, User user){
        PermissionType permissionType = permissionCreateRequest.getPermissionType();
        ResourceType resourceType = permissionCreateRequest.getResourceType();
        Long resourceId = permissionCreateRequest.getResourceId();
        Long userId = permissionCreateRequest.getUserId(); //TODO: validation for user ID
        User otherUser = new User(userId);

        if(user.getId().equals(userId))
            throw new ApiException("Can't give permission to yourself", HttpStatus.BAD_REQUEST);

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

        PermissionEntity existingPermission = permissionRepository.findByResourceIdAndResourceTypeAndUserId(resourceId, resourceType, userId);
        if(existingPermission != null){
            existingPermission.setPermissionType(permissionType);
            permissionRepository.save(existingPermission);
            return;
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

    @Transactional
    public void deletePermission(Long permissionId, User user){
        PermissionEntity permission = permissionRepository.findPermissionEntityById(permissionId);
        permissionExistenceRequiredValidation(permission);
        permissionModificationAuthorization(permission, user);

        permissionRepository.deleteById(permissionId);
    }

    @Transactional
    public void modifyPermission(Long permissionId, PermissionModifyRequest permissionModifyRequest, User user){
        PermissionEntity permission = permissionRepository.findPermissionEntityById(permissionId);
        permissionExistenceRequiredValidation(permission);
        permissionModificationAuthorization(permission, user);

        PermissionType permissionType = permissionModifyRequest.getPermissionType();
        if(permission.getPermissionType() == PermissionType.READ){
            if(permissionType == PermissionType.READ)
                throw new ApiException("Read permission already granted", HttpStatus.BAD_REQUEST);
            permission.setPermissionType(PermissionType.WRITE);
        }
        else{
            if(permissionType == PermissionType.WRITE)
                throw new ApiException("Write permission already granted", HttpStatus.BAD_REQUEST);
            FolderEntity folder;
            if(permission.getResourceType() == ResourceType.FOLDER)
                folder = folderRepository.findFolderEntityById(permission.getResourceId());
            else{
                FileEntity file = fileRepository.findFileEntityById(permission.getResourceId());
                folder = folderRepository.findFolderEntityById(file.getParentFolderId());
            }
            boolean[] doesReadPermissionExist = { false };
            folderService.forEachAncestor(folder, (current) -> {
                PermissionEntity currentPermission = permissionRepository.findByResourceIdAndResourceTypeAndUserId(current.getId(), ResourceType.FOLDER, permission.getUserId());
                if(currentPermission != null && currentPermission.getPermissionType() == PermissionType.READ){
                    doesReadPermissionExist[0] = true;
                    return false;
                }
                return true;
            });
            if(doesReadPermissionExist[0]){
                permissionRepository.deleteById(permissionId);
                return;
            }
            else{
                permission.setPermissionType(PermissionType.READ);
            }
        }
        permissionRepository.save(permission);
    }
}
