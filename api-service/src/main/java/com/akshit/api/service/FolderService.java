package com.akshit.api.service;

import com.akshit.api.entity.*;
import com.akshit.api.model.File;
import com.akshit.api.model.Folder;
import com.akshit.api.model.FolderContentsResponse;
import com.akshit.api.model.User;
import com.akshit.api.repo.FileRepository;
import com.akshit.api.repo.FolderRepository;
import com.akshit.api.repo.PermissionRepository;
import com.akshit.api.repo.UserRootFolderMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FolderService {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private UserRootFolderMappingRepository userRootFolderMappingRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private FileRepository fileRepository;

    private FolderEntity getParentFolder(FolderEntity folder){
        Long parentFolderId = folder.getParentFolderId();
        if(parentFolderId == null) return null;
        return folderRepository.findFolderEntityById(parentFolderId);
    }

    private FolderEntity getRootFolder(FolderEntity folder){
        FolderEntity currentFolder = folder;
        while(true){
            FolderEntity parentFolder = getParentFolder(currentFolder);
            if(parentFolder == null) break;
            currentFolder = parentFolder;
        }
        return currentFolder;
    }

    private UserRootFolderMappingEntity createUserRootFolderMapping(User user){
        FolderEntity rootFolder = folderRepository.save(new FolderEntity());

        UserRootFolderMappingEntity userRootFolderMapping = UserRootFolderMappingEntity
                .builder()
                .userId(user.getId())
                .folderId(rootFolder.getId())
                .build();
        userRootFolderMapping = userRootFolderMappingRepository.save(userRootFolderMapping);

        return userRootFolderMapping;
    }

    private boolean checkIfFolderIsOwnedByUser(FolderEntity folder, User user){
        FolderEntity rootFolder = getRootFolder(folder);
        UserRootFolderMappingEntity mapping = userRootFolderMappingRepository.findByFolderId(rootFolder.getId());
        return (mapping.getUserId() == user.getId());
    }

    private PermissionType getFolderPermissionForUser(FolderEntity folder, User user){
        FolderEntity currentFolder = folder;
        while(true){
            PermissionEntity permission = permissionRepository.findByResourceIdAndResourceTypeAndUserId(
                    folder.getId(),
                    ResourceType.FOLDER,
                    user.getId());
            if(permission != null) return permission.getPermissionType();
            FolderEntity parentFolder = getParentFolder(currentFolder);
            if(parentFolder == null) break;
            currentFolder = parentFolder;
        }
        if(checkIfFolderIsOwnedByUser(currentFolder, user)) return  PermissionType.WRITE;
        return null;
    }

    public UserRootFolderMappingEntity createUserRootFolderMappingIfNotExists(User user){
        UserRootFolderMappingEntity userRootFolderMapping = userRootFolderMappingRepository.findByUserId(user.getId());
        if(userRootFolderMapping != null) return userRootFolderMapping;
        return createUserRootFolderMapping(user);
    }

    public ResponseEntity<FolderContentsResponse> getFolderContents(Long folderId, User user){
        FolderEntity folder = folderRepository.findFolderEntityById(folderId);
        PermissionType permission = getFolderPermissionForUser(folder, user);
        if(permission == null)
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();

        List<FolderEntity> folders = folderRepository.findAllByParentFolderId(folderId);
        List<FileEntity> files = fileRepository.findAllByParentFolderId(folderId);
        return ResponseEntity.ok(
                FolderContentsResponse
                        .builder()
                        .folders(folders.stream().map(Folder::fromEntity).toList())
                        .files(files.stream().map(File::fromEntity).toList())
                        .build()
        );
    }
}
