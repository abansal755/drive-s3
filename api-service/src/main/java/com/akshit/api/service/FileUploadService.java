package com.akshit.api.service;

import com.akshit.api.entity.FileEntity;
import com.akshit.api.entity.FileUploadEntity;
import com.akshit.api.entity.UploadStatus;
import com.akshit.api.exception.ApiException;
import com.akshit.api.model.UploadStatusResponse;
import com.akshit.api.model.User;
import com.akshit.api.repo.FileRepository;
import com.akshit.api.repo.FileUploadRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.*;

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

    @Autowired
    private S3Service s3Service;

    @Autowired
    private TempStorageService tempStorageService;

    public void fileUploadExistenceRequiredValidation(FileUploadEntity fileUpload){
        if(fileUpload == null)
            throw new ApiException("Upload ID not found", HttpStatus.NOT_FOUND);
    }

    public void fileUploadMatchUserValidation(FileUploadEntity fileUpload, User user){
        if(!fileUpload.getUserId().equals(user.getId()))
            throw new ApiException("User doesn't have access to this upload ID", HttpStatus.FORBIDDEN);
    }

    public UploadStatusResponse getUploadStatus(Long uploadId,User user){
        FileUploadEntity fileUpload = fileUploadRepository.findFileUploadEntityById(uploadId);
        fileUploadExistenceRequiredValidation(fileUpload);
        fileUploadMatchUserValidation(fileUpload, user);

        return UploadStatusResponse
                        .builder()
                        .uploadStatus(fileUpload.getUploadStatus())
                        .build();
    }

    public void upload(HttpServletRequest request, Long uploadId, User user) throws IOException {
        // validations
        FileUploadEntity fileUpload = fileUploadRepository.findFileUploadEntityById(uploadId);
        fileUploadExistenceRequiredValidation(fileUpload);
        fileUploadMatchUserValidation(fileUpload, user);

        if(fileUpload.getUploadStatus() != UploadStatus.NOT_STARTED)
            throw new ApiException("Upload has already been started or completed", HttpStatus.FORBIDDEN);

        // set status to uploading
        fileUpload.setUploadStatus(UploadStatus.UPLOADING);
        fileUploadRepository.save(fileUpload);

        // upload
        FileEntity file = fileRepository.findFileEntityById(fileUpload.getFileId());
        String fileName = file.getId().toString();
        BufferedInputStream inputStream = new BufferedInputStream(request.getInputStream());
        tempStorageService.downloadStreamToFile(inputStream, fileName);
        s3Service.putS3Object(fileName, tempStorageService.getPath(fileName));
        tempStorageService.deleteFileIfExists(fileName);

        // update status to uploaded
        fileUpload.setUploadStatus(UploadStatus.UPLOADED);
        fileUploadRepository.save(fileUpload);

        // update file's s3 info
        Long size = s3Service.getS3ObjectSize(fileName);
        file.setS3BucketName(S3_BUCKET_NAME);
        file.setS3ObjectKey(fileName);
        file.setSizeInBytes(size);
        fileRepository.save(file);
    }
}
