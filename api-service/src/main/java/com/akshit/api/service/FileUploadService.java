package com.akshit.api.service;

import com.akshit.api.SharedResources;
import com.akshit.api.StreamCloserOnAbortThread;
import com.akshit.api.entity.FileEntity;
import com.akshit.api.entity.FileUploadEntity;
import com.akshit.api.entity.UploadStatus;
import com.akshit.api.exception.ApiException;
import com.akshit.api.model.User;
import com.akshit.api.repo.FileRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;

@Service
public class FileUploadService {

    @Autowired
    private FileRepository fileRepository;

//    @Value("${s3.bucket-name}")
    private String S3_BUCKET_NAME = null;

    @Autowired
    private FileHandlingService fileHandlingService;

    @Autowired
    private SharedResources sharedResources;

    private void uploadFileFromInputStream(FileEntity file, FileUploadEntity fileUploadEntity, InputStream inputStream, long contentLength) throws InterruptedException {
        String fileName = file.getId().toString();
        Thread thread = new StreamCloserOnAbortThread(fileUploadEntity, inputStream);
        thread.start();
        try {
            fileHandlingService.putObject(fileName, inputStream, contentLength);
        }
        finally {
            if(fileUploadEntity.getUploadStatus().get() != UploadStatus.ABORTED)
                thread.interrupt();
        }
        thread.join();
    }

    public void fileUploadExistenceRequiredValidation(FileUploadEntity fileUpload){
        if(fileUpload == null)
            throw new ApiException("Upload ID not found", HttpStatus.NOT_FOUND);
    }

    public void fileUploadMatchUserValidation(FileUploadEntity fileUpload, User user){
        if(!fileUpload.getUserId().equals(user.getId()))
            throw new ApiException("User doesn't have access to this upload ID", HttpStatus.FORBIDDEN);
    }

    public void upload(HttpServletRequest request, String uploadId, User user) throws IOException, InterruptedException {
        // validations
        FileUploadEntity fileUpload = sharedResources.fileUploads.get(uploadId);
        fileUploadExistenceRequiredValidation(fileUpload);
        fileUploadMatchUserValidation(fileUpload, user);

        boolean isUpdated = fileUpload.getUploadStatus().compareAndSet(UploadStatus.NOT_STARTED, UploadStatus.UPLOADING);
        if(!isUpdated)
            throw new ApiException("Upload cannot be started", HttpStatus.FORBIDDEN);

        // upload
        FileEntity file = fileRepository.findFileEntityById(fileUpload.getFileId());
        String fileName = file.getId().toString();
        long contentLength = Long.parseLong(request.getHeader("x-content-length"));
        BufferedInputStream inputStream = new BufferedInputStream(request.getInputStream());
        uploadFileFromInputStream(file, fileUpload, inputStream, contentLength);

        // remove from fileUploads
        sharedResources.fileUploads.remove(uploadId);

        // update file's s3 info
        Long size = fileHandlingService.getObjectSize(fileName);
        file.setS3BucketName(S3_BUCKET_NAME);
        file.setS3ObjectKey(fileName);
        file.setSizeInBytes(size);
        fileRepository.save(file);
    }

    @Transactional
    public void abort(String uploadId, User user){
        FileUploadEntity fileUpload = sharedResources.fileUploads.get(uploadId);
        fileUploadExistenceRequiredValidation(fileUpload);
        fileUploadMatchUserValidation(fileUpload, user);

        boolean isUpdated = fileUpload.getUploadStatus().compareAndSet(UploadStatus.UPLOADING, UploadStatus.ABORTED);
        if(!isUpdated)
            throw new ApiException("Upload cannot be aborted", HttpStatus.BAD_REQUEST);

        fileUpload.getUploadStatus().set(UploadStatus.ABORTED);
        fileRepository.deleteById(fileUpload.getFileId());
    }
}
