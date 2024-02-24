package com.akshit.api.service;

import com.akshit.api.entity.*;
import com.akshit.api.exception.ApiException;
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

    public void folderExistenceRequiredValidation(FolderEntity folder) throws ApiException {
        if(folder == null)
            throw new ApiException("Folder not found", HttpStatus.NOT_FOUND);
    }

    public void folderReadPermissionRequiredValidation(PermissionType permission) throws ApiException {
        if(permission == null)
            throw new ApiException("User doesn't have read permission for this folder", HttpStatus.FORBIDDEN);
    }
    
    public void folderWritePermissionRequiredValidation(PermissionType permission) throws ApiException {
        if(permission != PermissionType.WRITE)
            throw new ApiException("User doesn't have write permission for this folder", HttpStatus.FORBIDDEN);
    }

    public FolderContentsResponse getFolderContents(Long folderId, User user) throws ApiException {
        FolderEntity folder = folderRepository.findFolderEntityById(folderId);
        folderExistenceRequiredValidation(folder);

        PermissionType permission = getFolderPermissionForUser(folder, user);
        folderReadPermissionRequiredValidation(permission);

        List<FolderEntity> folders = getChildFolders(folder);
        List<FileEntity> files = getChildFiles(folder);
        return FolderContentsResponse
                .builder()
                .folders(folders.stream().map(Folder::fromEntity).toList())
                .files(files.stream().map(File::fromEntity).toList())
                .build();
    }

    public Folder createFolder(FolderCreateRequest folderCreateRequest, User user) throws ApiException {
        FolderEntity parentFolder = folderRepository.findFolderEntityById(folderCreateRequest.getParentFolderId());
        folderExistenceRequiredValidation(parentFolder);
        
        PermissionType permission = getFolderPermissionForUser(parentFolder, user);
        folderWritePermissionRequiredValidation(permission);
        
        FolderEntity folder = folderRepository.findByFolderNameAndParentFolderId(
                folderCreateRequest.getFolderName(),
                parentFolder.getId());
        if(folder != null)
            throw new ApiException("Folder with the same name already exists", HttpStatus.BAD_REQUEST);

        folder = folderRepository.save(FolderEntity
                    .builder()
                    .folderName(folderCreateRequest.getFolderName())
                    .parentFolderId(parentFolder.getId())
                    .createdAt(new Date().getTime())
                    .build());
        return Folder.fromEntity(folder);
    }

    public Folder updateFolder(Long folderId, FolderUpdateRequest folderUpdateRequest, User user) throws ApiException {
        FolderEntity folder = folderRepository.findFolderEntityById(folderId);
        folderExistenceRequiredValidation(folder);

        if(folder.getParentFolderId() == null)
            throw new ApiException("Cannot modify root folder", HttpStatus.BAD_REQUEST);

        PermissionType permission = getFolderPermissionForUser(folder, user);
        folderWritePermissionRequiredValidation(permission);

        folder.setFolderName(folderUpdateRequest.getFolderName());
        folder = folderRepository.save(folder);
        return Folder.fromEntity(folder);
    }

    public void deleteFolder(Long folderId, User user) throws ApiException {
        FolderEntity folder = folderRepository.findFolderEntityById(folderId);
        folderExistenceRequiredValidation(folder);

        if(folder.getParentFolderId() == null)
            throw new ApiException("Cannot delete root folder", HttpStatus.BAD_REQUEST);

        PermissionType permission = getFolderPermissionForUser(folder, user);
        folderWritePermissionRequiredValidation(permission);

        deleteFolderTree(folder);
    }

    public Folder getRootFolderForUser(User user){
        UserRootFolderMappingEntity mapping = userRootFolderMappingRepository.findByUserId(user.getId());
        FolderEntity rootFolder = folderRepository.findFolderEntityById(mapping.getFolderId());
        return Folder.fromEntity(rootFolder);
    }
}
