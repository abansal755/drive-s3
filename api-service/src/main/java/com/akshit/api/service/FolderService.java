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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static java.util.concurrent.CompletableFuture.supplyAsync;

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

    @Autowired
    private AuthService authService;

    @Transactional(propagation = Propagation.MANDATORY)
    public FolderEntity getParentFolder(FolderEntity folder){
        Long parentFolderId = folder.getParentFolderId();
        if(parentFolderId == null) return null;
        return folderRepository.findFolderEntityById(parentFolderId);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void forEachAncestor(FolderEntity folder, Function<FolderEntity, Boolean> function){
        FolderEntity currentFolder = folder;
        while(true){
            if(!function.apply(currentFolder)) break;
            FolderEntity parentFolder = getParentFolder(currentFolder);
            if(parentFolder == null) break;
            currentFolder = parentFolder;
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void breadthFirstSearch(FolderEntity folder, Function<FolderEntity, Boolean> function){
        ArrayDeque<FolderEntity> deque = new ArrayDeque<>();
        deque.addLast(folder);
        while(!deque.isEmpty()){
            FolderEntity front = deque.poll();
            if(!function.apply(front)) break;
            List<FolderEntity> children = getChildFolders(front);
            children.forEach(deque::addLast);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public FolderEntity getRootFolder(FolderEntity folder){
        FolderEntity[] root = { null };
        forEachAncestor(folder, (currentFolder) -> {
            root[0] = currentFolder;
            return true;
        });
        return root[0];
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public UserRootFolderMappingEntity createUserRootFolderMapping(User user){
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

    @Transactional(propagation = Propagation.MANDATORY)
    public Long getFolderOwnerId(FolderEntity folder){
        FolderEntity rootFolder = getRootFolder(folder);
        UserRootFolderMappingEntity mapping = userRootFolderMappingRepository.findByFolderId(rootFolder.getId());
        return mapping.getUserId();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public boolean checkIfFolderIsOwnedByUser(FolderEntity folder, User user){
        Long ownerId = getFolderOwnerId(folder);
        return (ownerId.equals(user.getId()));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public PermissionType getFolderPermissionForUser(FolderEntity folder, User user){
        PermissionType[] permissionType = { null };
        FolderEntity[] rootFolder = { null };
        forEachAncestor(folder, (currentFolder) -> {
            rootFolder[0] = currentFolder;
            PermissionEntity permission = permissionRepository.findByResourceIdAndResourceTypeAndUserId(
                    currentFolder.getId(),
                    ResourceType.FOLDER,
                    user.getId()
            );
            if(permission != null){
                permissionType[0] = permission.getPermissionType();
                return false;
            }
            return true;
        });
        if(permissionType[0] != null)
            return permissionType[0];
        if(checkIfFolderIsOwnedByUser(rootFolder[0], user)) return PermissionType.WRITE;
        return null;
    }

    public List<FolderEntity> getChildFolders(FolderEntity folder){
        return folderRepository.findAllByParentFolderId(folder.getId());
    }

    public List<FileEntity> getChildFiles(FolderEntity folder){
        return fileRepository.findAllByParentFolderId(folder.getId());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void deleteFolder(FolderEntity folder){
        permissionRepository.deleteAllByResourceIdAndResourceType(folder.getId(), ResourceType.FOLDER);
        folderRepository.deleteById(folder.getId());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void deleteFolderTree(FolderEntity folder){
        breadthFirstSearch(folder, (currentFolder) -> {
            deleteFolder(currentFolder);
            List<FileEntity> files = getChildFiles(currentFolder);
            files.forEach((file) -> fileService.deleteFile(file.getId()));
            return true;
        });
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Long getFolderSize(FolderEntity folder){
        Long[] size = { 0L };
        breadthFirstSearch(folder, (currentFolder) -> {
            List<FileEntity> childFiles = getChildFiles(currentFolder);
            childFiles.forEach(file -> size[0]+= file.getSizeInBytes());
            return true;
        });
        return size[0];
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void addPermissionTypeToFolder(Folder folder, PermissionType parentFolderPermission, User user){
        folder.setPermissionType(parentFolderPermission);
        if(parentFolderPermission == PermissionType.READ){
            PermissionEntity current = permissionRepository.findByResourceIdAndResourceTypeAndUserId(folder.getId(), ResourceType.FOLDER, user.getId());
            if(current != null && current.getPermissionType() == PermissionType.WRITE)
                folder.setPermissionType(PermissionType.WRITE);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void addPermissionTypeToFile(File file, PermissionType parentFolderPermission, User user){
        file.setPermissionType(parentFolderPermission);
        if(parentFolderPermission == PermissionType.READ){
            PermissionEntity current = permissionRepository.findByResourceIdAndResourceTypeAndUserId(file.getId(), ResourceType.FILE, user.getId());
            if(current != null && current.getPermissionType() == PermissionType.WRITE)
                file.setPermissionType(PermissionType.WRITE);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void deleteAllPermissionsInChildren(FolderEntity folder, User user, PermissionType permissionType){
        breadthFirstSearch(folder, (current) -> {
            List<FileEntity> childFiles = getChildFiles(current);
            childFiles.forEach(file -> permissionRepository.deleteAllByResourceIdAndResourceTypeAndPermissionTypeAndUserId(file.getId(), ResourceType.FILE, permissionType, user.getId()));
            if(current.getId().equals(folder.getId()))
                return true;
            permissionRepository.deleteAllByResourceIdAndResourceTypeAndPermissionTypeAndUserId(current.getId(), ResourceType.FOLDER, permissionType, user.getId());
            return true;
        });
    }

    public void folderExistenceRequiredValidation(FolderEntity folder){
        if(folder == null)
            throw new ApiException("Folder not found", HttpStatus.NOT_FOUND);
    }

    public void folderReadPermissionRequiredValidation(PermissionType permission){
        if(permission == null)
            throw new ApiException("User doesn't have read permission for this folder", HttpStatus.FORBIDDEN);
    }
    
    public void folderWritePermissionRequiredValidation(PermissionType permission){
        if(permission != PermissionType.WRITE)
            throw new ApiException("User doesn't have write permission for this folder", HttpStatus.FORBIDDEN);
    }

    @Transactional
    public UserRootFolderMappingEntity createUserRootFolderMappingIfNotExists(User user){
        UserRootFolderMappingEntity userRootFolderMapping = userRootFolderMappingRepository.findByUserId(user.getId());
        if(userRootFolderMapping != null) return userRootFolderMapping;
        return createUserRootFolderMapping(user);
    }

    @Transactional
    public FolderContentsResponse getFolderContents(Long folderId, User user){
        FolderEntity folder = folderRepository.findFolderEntityById(folderId);
        folderExistenceRequiredValidation(folder);

        PermissionType permission = getFolderPermissionForUser(folder, user);
        folderReadPermissionRequiredValidation(permission);

        List<FolderEntity> folders = getChildFolders(folder);
        List<FileEntity> files = getChildFiles(folder);
        return FolderContentsResponse
                .builder()
                .folders(folders
                        .stream()
                        .map(Folder::fromEntity)
                        .peek(fol -> addPermissionTypeToFolder(fol, permission, user))
                        .toList()
                )
                .files(files
                        .stream()
                        .map(File::fromEntity)
                        .peek(file -> addPermissionTypeToFile(file, permission, user))
                        .toList()
                )
                .build();
    }

    @Transactional
    public Folder createFolder(FolderCreateRequest folderCreateRequest, User user){
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

    @Transactional
    public Folder updateFolder(Long folderId, FolderUpdateRequest folderUpdateRequest, User user){
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

    @Transactional
    public void deleteFolder(Long folderId, User user){
        FolderEntity folder = folderRepository.findFolderEntityById(folderId);
        folderExistenceRequiredValidation(folder);

        if(folder.getParentFolderId() == null)
            throw new ApiException("Cannot delete root folder", HttpStatus.BAD_REQUEST);

        PermissionType permission = getFolderPermissionForUser(folder, user);
        folderWritePermissionRequiredValidation(permission);

        deleteFolderTree(folder);
    }

    @Transactional
    public Folder getRootFolderForUser(User user){
        UserRootFolderMappingEntity mapping = userRootFolderMappingRepository.findByUserId(user.getId());
        FolderEntity rootFolder = folderRepository.findFolderEntityById(mapping.getFolderId());
        return Folder.fromEntity(rootFolder);
    }

    @Transactional
    public AncestorsResponse getFolderAncestors(Long folderId, User user){
        FolderEntity folder = folderRepository.findFolderEntityById(folderId);
        folderExistenceRequiredValidation(folder);

        List<FolderEntity> ancestors = new ArrayList<>();

        int[] lastAccessibleFolder = { -1 };
        FolderEntity[] rootFolder = { null };
        PermissionEntity[] userPermission = { null };
        forEachAncestor(folder, (currentFolder) -> {
            ancestors.add(currentFolder);
            rootFolder[0] = currentFolder;

            PermissionEntity permission = permissionRepository.findByResourceIdAndResourceTypeAndUserId(
                    currentFolder.getId(),
                    ResourceType.FOLDER,
                    user.getId());
            if(permission != null){
                lastAccessibleFolder[0] = ancestors.size() - 1;
                if(userPermission[0] == null) userPermission[0] = permission;
            }
            return true;
        });

        if((lastAccessibleFolder[0] == -1) && !checkIfFolderIsOwnedByUser(rootFolder[0], user))
            throw new ApiException("User doesn't have read permission for this folder", HttpStatus.FORBIDDEN);

        List<FolderEntity> ancestorsRes = ancestors;
        if(lastAccessibleFolder[0] != -1)
            ancestorsRes = ancestors.subList(0, lastAccessibleFolder[0] + 1);
        Collections.reverse(ancestorsRes);

        Long ownerId = getFolderOwnerId(rootFolder[0]);
        User owner = authService.getUserById(ownerId);

        PermissionType permissionType;
        if(userPermission[0] != null)
            permissionType = userPermission[0].getPermissionType();
        else
            permissionType = PermissionType.WRITE;

        return AncestorsResponse
                .builder()
                .ancestors(
                        ancestorsRes
                                .stream()
                                .map(Folder::fromEntity)
                                .toList()
                )
                .rootFolderOwner(owner)
                .permissionType(permissionType)
                .build();
    }

    @Transactional
    public FolderSizeResponse getFolderSize(Long folderId, User user){
        FolderEntity folder = folderRepository.findFolderEntityById(folderId);
        folderExistenceRequiredValidation(folder);

        PermissionType permission = getFolderPermissionForUser(folder, user);
        folderReadPermissionRequiredValidation(permission);

        return FolderSizeResponse
                .builder()
                .sizeInBytes(getFolderSize(folder))
                .build();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public List<CompletableFuture<PermissionResponse>> getPermissionsGranted(FolderEntity folder){
        List<CompletableFuture<PermissionResponse>> completableFutures = new ArrayList<>();
        forEachAncestor(folder, (current) -> {
            List<PermissionEntity> permissions = permissionRepository.findAllByResourceIdAndResourceType(current.getId(), ResourceType.FOLDER);
            permissions.forEach(permission -> {
                completableFutures.add(PermissionResponse.fromPermissionEntity(
                        permission,
                        authService::getUserById,
                        !current.getId().equals(folder.getId())));
            });
            return true;
        });
        return completableFutures;
    }

    @Transactional
    public List<PermissionResponse> getPermissionsGranted(Long folderId, User user){
        FolderEntity folder = folderRepository.findFolderEntityById(folderId);
        folderExistenceRequiredValidation(folder);

        if(!checkIfFolderIsOwnedByUser(folder, user))
            throw new ApiException("Not allowed to get the permissions granted for this folder", HttpStatus.FORBIDDEN);

        List<CompletableFuture<PermissionResponse>> completableFutures = getPermissionsGranted(folder);
        return completableFutures
                .stream()
                .map(CompletableFuture::join)
                .toList();
    }
}
