package com.akshit.api.service;

import com.akshit.api.entity.DownloadStatus;
import com.akshit.api.entity.FileEntity;
import com.akshit.api.entity.FileUploadEntity;
import com.akshit.api.entity.UploadStatus;
import com.akshit.api.exception.ApiException;
import com.akshit.api.model.UploadStatusResponse;
import com.akshit.api.model.User;
import com.akshit.api.repo.FileRepository;
import com.akshit.api.repo.FileUploadRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private EntityManager entityManager;

    private void uploadFileFromInputStream(FileEntity file, Long fileUploadId, InputStream inputStream, long contentLength){
        String fileName = file.getId().toString();
        Thread thread = new Thread(() -> {
            try {
                while(true){
                    entityManager.clear();
                    UploadStatus status = fileUploadRepository.findFileUploadEntityById(fileUploadId).getUploadStatus();
                    if(status == UploadStatus.ABORTED){
                        inputStream.close();
                        break;
                    }
                    Thread.sleep(3_000);
                }
            }
            catch (Exception ex){
                System.err.println(ex);
            }
        });
        thread.start();
        try {
            s3Service.putS3Object(fileName, inputStream, contentLength);
        }
        finally {
            thread.interrupt();
        }
    }

    public void fileUploadExistenceRequiredValidation(FileUploadEntity fileUpload){
        if(fileUpload == null)
            throw new ApiException("Upload ID not found", HttpStatus.NOT_FOUND);
    }

    public void fileUploadMatchUserValidation(FileUploadEntity fileUpload, User user){
        if(!fileUpload.getUserId().equals(user.getId()))
            throw new ApiException("User doesn't have access to this upload ID", HttpStatus.FORBIDDEN);
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
        long contentLength = Long.parseLong(request.getHeader("x-content-length"));
        BufferedInputStream inputStream = new BufferedInputStream(request.getInputStream());
        uploadFileFromInputStream(file, fileUpload.getId(), inputStream, contentLength);

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

    @Transactional
    public void abort(Long uploadId, User user){
        FileUploadEntity fileUpload = fileUploadRepository.findFileUploadEntityById(uploadId);
        fileUploadExistenceRequiredValidation(fileUpload);
        fileUploadMatchUserValidation(fileUpload, user);

        UploadStatus status = fileUpload.getUploadStatus();
        if(status == UploadStatus.NOT_STARTED)
            throw new ApiException("Upload has not been started yet", HttpStatus.BAD_REQUEST);
        if(status == UploadStatus.UPLOADED)
            throw new ApiException("Upload has already been completed", HttpStatus.BAD_REQUEST);
        if(status == UploadStatus.ABORTED)
            throw new ApiException("Upload has already been aborted", HttpStatus.BAD_REQUEST);

        fileUpload.setUploadStatus(UploadStatus.ABORTED);
        fileUploadRepository.save(fileUpload);
        fileRepository.deleteById(fileUpload.getFileId());
    }
}
