package com.akshit.api.service;

import com.akshit.api.entity.FileEntity;
import com.akshit.api.entity.FileUploadEntity;
import com.akshit.api.entity.UploadStatus;
import com.akshit.api.model.UploadStatusResponse;
import com.akshit.api.model.User;
import com.akshit.api.repo.FileRepository;
import com.akshit.api.repo.FileUploadRepository;
import com.akshit.api.utils.FileIOUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.akshit.api.utils.FileIOUtils.*;

@Service
public class FileUploadService {

    @Autowired
    private FileUploadRepository fileUploadRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private S3Client s3Client;

    @Value("${s3.bucket-name}")
    private String S3_BUCKET_NAME;

    @Value("${temp-file-storage}")
    private String TEMP_FILE_STORAGE_DIR;

    public ResponseEntity<UploadStatusResponse> getUploadStatus(Long uploadId,User user){
        FileUploadEntity fileUpload = fileUploadRepository.findFileUploadEntityById(uploadId);
        if(fileUpload == null)
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        if(!fileUpload.getUserId().equals(user.getId()))
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();

        return ResponseEntity.ok(UploadStatusResponse
                        .builder()
                        .uploadStatus(fileUpload.getUploadStatus())
                        .build());
    }

    public ResponseEntity<Void> upload(HttpServletRequest request, Long uploadId, User user) throws IOException {
        // validations
        FileUploadEntity fileUpload = fileUploadRepository.findFileUploadEntityById(uploadId);
        if(fileUpload == null)
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        if(!fileUpload.getUserId().equals(user.getId()))
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        if(fileUpload.getUploadStatus() != UploadStatus.NOT_STARTED)
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();

        // set status to uploading
        fileUpload.setUploadStatus(UploadStatus.UPLOADING);
        fileUploadRepository.save(fileUpload);

        // upload
        FileEntity file = fileRepository.findFileEntityById(fileUpload.getFileId());
        String fileName = file.getId().toString();
        BufferedInputStream inputStream = new BufferedInputStream(request.getInputStream());
        createDirectoryHierarchyIfNotExists(TEMP_FILE_STORAGE_DIR);
        downloadStreamToFile(inputStream, TEMP_FILE_STORAGE_DIR + File.separator + fileName);
        putS3Object(s3Client, S3_BUCKET_NAME, fileName, TEMP_FILE_STORAGE_DIR + File.separator + fileName);
        deleteFileIfExists(TEMP_FILE_STORAGE_DIR + File.separator + fileName);

        // update status to uploaded
        fileUpload.setUploadStatus(UploadStatus.UPLOADED);
        fileUploadRepository.save(fileUpload);

        // update file's s3 info
        file.setS3BucketName(S3_BUCKET_NAME);
        file.setS3ObjectKey(fileName);
        fileRepository.save(file);

        return ResponseEntity.ok().build();
    }
}
