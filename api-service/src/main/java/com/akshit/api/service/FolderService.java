package com.akshit.api.service;

import com.akshit.api.entity.*;
import com.akshit.api.model.*;
import com.akshit.api.repo.FileRepository;
import com.akshit.api.repo.FolderRepository;
import com.akshit.api.repo.PermissionRepository;
import com.akshit.api.repo.UserRootFolderMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Date;
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

    @Autowired
    private FileService fileService;

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
        FolderEntity rootFolder = folderRepository.save(
                FolderEntity
                        .builder()
                        .createdAt(new Date().getTime())
                        .build());

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

    public PermissionType getFolderPermissionForUser(FolderEntity folder, User user){
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

    private List<FolderEntity> getChildFolders(FolderEntity folder){
        return folderRepository.findAllByParentFolderId(folder.getId());
    }

    private List<FileEntity> getChildFiles(FolderEntity folder){
        return fileRepository.findAllByParentFolderId(folder.getId());
    }

    private void deleteFolder(FolderEntity folder){
        permissionRepository.deleteAllByResourceIdAndResourceType(folder.getId(), ResourceType.FOLDER);
        folderRepository.deleteById(folder.getId());
    }

    private void deleteFolderTree(FolderEntity folder){
        ArrayDeque<FolderEntity> deque = new ArrayDeque<>();
        deque.addLast(folder);
        while(!deque.isEmpty()){
            FolderEntity head = deque.poll();
            deleteFolder(head);

            List<FolderEntity> children = getChildFolders(head);
            for(FolderEntity child:children)
                deque.addLast(child);
            List<FileEntity> files = getChildFiles(folder);
            for(FileEntity file:files)
                fileService.deleteFile(file.getId());
        }
    }

    public UserRootFolderMappingEntity createUserRootFolderMappingIfNotExists(User user){
        UserRootFolderMappingEntity userRootFolderMapping = userRootFolderMappingRepository.findByUserId(user.getId());
        if(userRootFolderMapping != null) return userRootFolderMapping;
        return createUserRootFolderMapping(user);
    }

    public ResponseEntity<FolderContentsResponse> getFolderContents(Long folderId, User user){
        FolderEntity folder = folderRepository.findFolderEntityById(folderId);
        if(folder == null)
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        PermissionType permission = getFolderPermissionForUser(folder, user);
        if(permission == null)
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();

        List<FolderEntity> folders = getChildFolders(folder);
        List<FileEntity> files = getChildFiles(folder);
        return ResponseEntity.ok(
                FolderContentsResponse
                        .builder()
                        .folders(folders.stream().map(Folder::fromEntity).toList())
                        .files(files.stream().map(File::fromEntity).toList())
                        .build()
        );
    }

    public ResponseEntity<Folder> createFolder(FolderCreateRequest folderCreateRequest, User user){
        FolderEntity parentFolder = folderRepository.findFolderEntityById(folderCreateRequest.getParentFolderId());
        if(parentFolder == null)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        PermissionType permission = getFolderPermissionForUser(parentFolder, user);
        if((permission == null) || (permission == PermissionType.READ))
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        FolderEntity folder = folderRepository.findByFolderNameAndParentFolderId(
                folderCreateRequest.getFolderName(),
                parentFolder.getId());
        if(folder != null)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();

        folder = folderRepository.save(FolderEntity
                    .builder()
                    .folderName(folderCreateRequest.getFolderName())
                    .parentFolderId(parentFolder.getId())
                    .createdAt(new Date().getTime())
                    .build());
        return ResponseEntity.ok(Folder.fromEntity(folder));
    }

    public ResponseEntity<Folder> updateFolder(Long folderId, FolderUpdateRequest folderUpdateRequest, User user){
        FolderEntity folder = folderRepository.findFolderEntityById(folderId);
        if(folder == null)
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        if(folder.getParentFolderId() == null)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        PermissionType permission = getFolderPermissionForUser(folder, user);
        if((permission == null) || (permission == PermissionType.READ))
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        String newFolderName = folderUpdateRequest.getFolderName();
        if((newFolderName == null) || (newFolderName.equals(folder.getFolderName())))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();

        folder.setFolderName(newFolderName);
        folder = folderRepository.save(folder);
        return ResponseEntity.ok(Folder.fromEntity(folder));
    }

    public ResponseEntity<Void> deleteFolder(Long folderId, User user){
        FolderEntity folder = folderRepository.findFolderEntityById(folderId);
        if(folder == null)
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        if(folder.getParentFolderId() == null)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        PermissionType permission = getFolderPermissionForUser(folder, user);
        if((permission == null) || (permission == PermissionType.READ))
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        deleteFolderTree(folder);
        return ResponseEntity.ok().build();
    }

    public Folder getRootFolderForUser(User user){
        UserRootFolderMappingEntity mapping = userRootFolderMappingRepository.findByUserId(user.getId());
        FolderEntity rootFolder = folderRepository.findFolderEntityById(mapping.getFolderId());
        return Folder.fromEntity(rootFolder);
    }
}
