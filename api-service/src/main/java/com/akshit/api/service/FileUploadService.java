package com.akshit.api.service;

import com.akshit.api.entity.FileEntity;
import com.akshit.api.entity.FileUploadEntity;
import com.akshit.api.entity.UploadStatus;
import com.akshit.api.model.UploadStatusResponse;
import com.akshit.api.model.User;
import com.akshit.api.repo.FileRepository;
import com.akshit.api.repo.FileUploadRepository;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.BufferedInputStream;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class FileUploadService {

    @Autowired
    private FileUploadRepository fileUploadRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private S3Client s3Client;

    @Value("${s3.bucket-name}")
    private String s3BucketName;

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

        fileUpload.setUploadStatus(UploadStatus.UPLOADING);
        fileUploadRepository.save(fileUpload);

        FileEntity file = fileRepository.findFileEntityById(fileUpload.getFileId());
        BufferedInputStream inputStream = new BufferedInputStream(request.getInputStream());
        long contentLength = Long.parseLong(request.getHeader("x-content-length"));

        String bucketName = s3BucketName;
        String objectKey = file.getId().toString();
        PutObjectRequest putObjectRequest = PutObjectRequest
                .builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));

        fileUpload.setUploadStatus(UploadStatus.UPLOADED);
        fileUploadRepository.save(fileUpload);

        file.setS3BucketName(bucketName);
        file.setS3ObjectKey(objectKey);
        fileRepository.save(file);

        return ResponseEntity.ok().build();
    }
}
