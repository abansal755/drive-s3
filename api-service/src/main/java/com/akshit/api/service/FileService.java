package com.akshit.api.service;

import com.akshit.api.entity.*;
import com.akshit.api.model.FileCreateRequest;
import com.akshit.api.model.FileCreateResponse;
import com.akshit.api.model.FileUpdateRequest;
import com.akshit.api.model.User;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.*;
import java.util.Date;

import static com.akshit.api.utils.FileIOUtils.*;

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
    private S3Client s3Client;

    @Autowired
    private PermissionRepository permissionRepository;

    @Value("${s3.bucket-name}")
    private String S3_BUCKET_NAME;

    @Value("${temp-file-storage}")
    private String TEMP_FILE_STORAGE_DIR;

    private PermissionType getFilePermissionForUser(FileEntity file, User user){
        PermissionEntity permission = permissionRepository.findByResourceIdAndResourceTypeAndUserId(file.getId(), ResourceType.FILE, user.getId());
        if(permission != null)
            return permission.getPermissionType();
        FolderEntity parentFolder = folderRepository.findFolderEntityById(file.getParentFolderId());
        return folderService.getFolderPermissionForUser(parentFolder, user);
    }

    public void deleteFile(Long fileId){
        fileRepository.deleteById(fileId);
        permissionRepository.deleteAllByResourceIdAndResourceType(fileId, ResourceType.FILE);
        deleteS3Object(s3Client, S3_BUCKET_NAME, fileId.toString());
    }

    public ResponseEntity<FileCreateResponse> createFile(FileCreateRequest fileCreateRequest, User user){
        FolderEntity parentFolder = folderRepository.findFolderEntityById(fileCreateRequest.getParentFolderId());
        if(parentFolder == null)
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        PermissionType permission = folderService.getFolderPermissionForUser(parentFolder, user);
        if(permission != PermissionType.WRITE)
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();

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
        return ResponseEntity.ok(FileCreateResponse
                        .builder()
                        .uploadId(fileUploadEntity.getId())
                        .build());
    }

    public ResponseEntity<StreamingResponseBody> downloadFile(Long fileId, User user) throws IOException {
        // validations
        FileEntity file = fileRepository.findFileEntityById(fileId);
        if(file == null)
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        PermissionType permission = getFilePermissionForUser(file, user);
        if(permission == null)
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();

        // download file from s3
        String fileName = file.getId().toString();
        BufferedInputStream inputStream = getS3Object(s3Client, S3_BUCKET_NAME, fileName);
        createDirectoryHierarchyIfNotExists(TEMP_FILE_STORAGE_DIR);
        downloadStreamToFile(inputStream, TEMP_FILE_STORAGE_DIR + File.separator + fileName);
        inputStream.close();

        return ResponseEntity.ok(
                (OutputStream outputStream) -> {
                    // read the downloaded file and stream it as response body
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(TEMP_FILE_STORAGE_DIR + File.separator + fileName));

                    int b;
                    while((b = bufferedInputStream.read()) != -1)
                        bufferedOutputStream.write(b);
                    bufferedOutputStream.flush();
                    bufferedInputStream.close();
                    deleteFileIfExists(TEMP_FILE_STORAGE_DIR + File.separator + fileName);
                });
    }

    public ResponseEntity<com.akshit.api.model.File> modifyFile(FileUpdateRequest fileUpdateRequest, Long fileId, User user){
        FileEntity file = fileRepository.findFileEntityById(fileId);
        if(file == null)
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        PermissionType permission = getFilePermissionForUser(file, user);
        if(permission != PermissionType.WRITE)
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();

        String newName = fileUpdateRequest.getName();
        String newExtension = fileUpdateRequest.getExtension();
        boolean nameChange = newName != null && !newName.equals(file.getName());
        boolean extensionChange = newExtension != null && !newExtension.equals(file.getExtension());

        if(!nameChange && !extensionChange)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        if(nameChange)
            file.setName(newName);
        if(extensionChange)
            file.setExtension(newExtension);
        file = fileRepository.save(file);
        return ResponseEntity.ok(com.akshit.api.model.File.fromEntity(file));
    }

    public ResponseEntity<Void> deleteFile(Long fileId, User user){
        FileEntity file = fileRepository.findFileEntityById(fileId);
        if(file == null)
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        PermissionType permission = getFilePermissionForUser(file, user);
        if(permission != PermissionType.WRITE)
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();

        deleteFile(file.getId());
        return ResponseEntity.ok().build();
    }
}
