package com.akshit.api.service;

import com.akshit.api.entity.*;
import com.akshit.api.exception.ApiException;
import com.akshit.api.model.*;
import com.akshit.api.repo.FileRepository;
import com.akshit.api.repo.FileUploadRepository;
import com.akshit.api.repo.FolderRepository;
import com.akshit.api.repo.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileUploadRepository fileUploadRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FolderService folderService;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private AuthService authService;

    @Transactional(propagation = Propagation.MANDATORY)
    public PermissionType getFilePermissionForUser(FileEntity file, User user){
        PermissionEntity permission = permissionRepository.findByResourceIdAndResourceTypeAndUserId(file.getId(), ResourceType.FILE, user.getId());
        if(permission != null)
            return permission.getPermissionType();
        FolderEntity parentFolder = folderRepository.findFolderEntityById(file.getParentFolderId());
        return folderService.getFolderPermissionForUser(parentFolder, user);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void deleteFile(Long fileId){
        fileRepository.deleteById(fileId);
        permissionRepository.deleteAllByResourceIdAndResourceType(fileId, ResourceType.FILE);
        s3Service.deleteS3Object(fileId.toString());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public boolean checkIfFileIsOwnedByUser(FileEntity file, User user){
        FolderEntity parentFolder = folderRepository.findFolderEntityById(file.getParentFolderId());
        return folderService.checkIfFolderIsOwnedByUser(parentFolder, user);
    }

    public void fileExistenceRequiredValidation(FileEntity file){
        if(file == null)
            throw new ApiException("File not found", HttpStatus.NOT_FOUND);
    }

    public void fileReadPermissionRequiredValidation(PermissionType permission){
        if(permission == null)
            throw new ApiException("User doesn't have read permission for this file", HttpStatus.FORBIDDEN);
    }

    public void fileWritePermissionRequiredValidation(PermissionType permission){
        if(permission != PermissionType.WRITE)
            throw new ApiException("User doesn't have write permission for this file", HttpStatus.FORBIDDEN);
    }

    @Transactional
    public FileCreateResponse createFile(FileCreateRequest fileCreateRequest, User user){
        FolderEntity parentFolder = folderRepository.findFolderEntityById(fileCreateRequest.getParentFolderId());
        if(parentFolder == null)
            throw new ApiException("Parent folder not found", HttpStatus.BAD_REQUEST);

        PermissionType permission = folderService.getFolderPermissionForUser(parentFolder, user);
        folderService.folderWritePermissionRequiredValidation(permission);

        FileEntity file = fileRepository.save(FileEntity
                        .builder()
                        .parentFolderId(fileCreateRequest.getParentFolderId())
                        .name(fileCreateRequest.getName())
                        .extension(fileCreateRequest.getExtension())
                        .createdAt(new Date().getTime())
                        .build());

        FileUploadEntity fileUploadEntity = fileUploadRepository.save(FileUploadEntity
                        .builder()
                        .userId(user.getId())
                        .fileId(file.getId())
                        .uploadStatus(UploadStatus.NOT_STARTED)
                        .build());
        return FileCreateResponse
                        .builder()
                        .uploadId(fileUploadEntity.getId())
                        .build();
    }

    @Transactional
    public com.akshit.api.model.File modifyFile(
            FileUpdateRequest fileUpdateRequest,
            Long fileId, User user)
    {
        FileEntity file = fileRepository.findFileEntityById(fileId);
        fileExistenceRequiredValidation(file);

        PermissionType permission = getFilePermissionForUser(file, user);
        fileWritePermissionRequiredValidation(permission);

        String newName = fileUpdateRequest.getName();
        if(newName != null)
            file.setName(newName);

        String newExtension = fileUpdateRequest.getExtension();
        if(newExtension != null)
            file.setExtension(newExtension);

        file = fileRepository.save(file);
        return com.akshit.api.model.File.fromEntity(file);
    }

    @Transactional
    public void deleteFile(Long fileId, User user){
        FileEntity file = fileRepository.findFileEntityById(fileId);
        fileExistenceRequiredValidation(file);

        PermissionType permission = getFilePermissionForUser(file, user);
        fileWritePermissionRequiredValidation(permission);

        deleteFile(file.getId());
    }

    @Transactional
    public List<PermissionResponse> getPermissionsGranted(Long fileId, User user){
        FileEntity file = fileRepository.findFileEntityById(fileId);
        fileExistenceRequiredValidation(file);

        if(!checkIfFileIsOwnedByUser(file, user))
            throw new ApiException("Not allowed to get the permissions granted for this file", HttpStatus.FORBIDDEN);

        List<CompletableFuture<PermissionResponse>> completableFutures = new ArrayList<>();
        permissionRepository
                .findAllByResourceIdAndResourceType(fileId, ResourceType.FILE)
                .forEach(permission -> {
                    completableFutures.add(PermissionResponse.fromPermissionEntity(
                            permission,
                            authService::getUserById,
                            false));
                });

        FolderEntity parentFolder = folderRepository.findFolderEntityById(file.getParentFolderId());
        completableFutures.addAll(folderService.getPermissionsGranted(parentFolder));
        return completableFutures
                .stream()
                .map(CompletableFuture::join)
                .toList();
    }
}
