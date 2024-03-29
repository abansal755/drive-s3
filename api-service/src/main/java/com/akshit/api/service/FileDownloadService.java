package com.akshit.api.service;

import com.akshit.api.entity.DownloadStatus;
import com.akshit.api.entity.FileDownloadEntity;
import com.akshit.api.entity.FileEntity;
import com.akshit.api.entity.PermissionType;
import com.akshit.api.exception.ApiException;
import com.akshit.api.model.DownloadInitiateResponse;
import com.akshit.api.model.User;
import com.akshit.api.repo.FileDownloadRepository;
import com.akshit.api.repo.FileRepository;
import com.akshit.api.repo.PermissionRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.Date;

@Service
public class FileDownloadService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileDownloadRepository fileDownloadRepository;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private EntityManager entityManager;

    private void fileDownloadExistenceRequiredValidation(FileDownloadEntity fileDownload){
        if(fileDownload == null)
            throw new ApiException("Download ID not found", HttpStatus.NOT_FOUND);
    }

    private void fileDownloadUserValidation(FileDownloadEntity fileDownload, User user){
        if(!fileDownload.getUserId().equals(user.getId()))
            throw new ApiException("Not allowed to download from this download ID", HttpStatus.FORBIDDEN);
    }

    @Transactional
    public DownloadInitiateResponse initiateDownload(Long fileId, User user){
        FileEntity file = fileRepository.findFileEntityById(fileId);
        fileService.fileExistenceRequiredValidation(file);

        PermissionType permission = fileService.getFilePermissionForUser(file, user);
        fileService.fileReadPermissionRequiredValidation(permission);

        FileDownloadEntity fileDownload = FileDownloadEntity
                .builder()
                .userId(user.getId())
                .fileId(fileId)
                .status(DownloadStatus.NOT_STARTED)
                .build();
        fileDownloadRepository.save(fileDownload);
        return DownloadInitiateResponse
                .builder()
                .downloadId(fileDownload.getId())
                .build();
    }

    @Transactional(propagation = Propagation.NEVER)
    public StreamingResponseBody download(Long downloadId, User user){
        FileDownloadEntity fileDownload = fileDownloadRepository.findFileDownloadEntityById(downloadId);
        fileDownloadExistenceRequiredValidation(fileDownload);
        fileDownloadUserValidation(fileDownload, user);

        FileEntity file = fileRepository.findFileEntityById(fileDownload.getFileId());
        fileService.fileExistenceRequiredValidation(file);

        String fileName = file.getId().toString();
        fileDownload.setStatus(DownloadStatus.DOWNLOADING);
        fileDownloadRepository.save(fileDownload);

        return (OutputStream outputStream) -> {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            BufferedInputStream inputStream = s3Service.getS3Object(fileName);

            int b;
            long lastStatusFetchTime = new Date().getTime();
            boolean isAborted = false;
            while((b = inputStream.read()) != -1){
                long currentTime = new Date().getTime();
                if(currentTime - lastStatusFetchTime >= 3_000){
                    lastStatusFetchTime = currentTime;
                    entityManager.clear();
                    DownloadStatus status = fileDownloadRepository.findFileDownloadEntityById(downloadId).getStatus();
                    if(status == DownloadStatus.ABORTED){
                        isAborted = true;
                        break;
                    }
                }
                bufferedOutputStream.write(b);
            }
            bufferedOutputStream.flush();
            inputStream.close();

            FileDownloadEntity download = fileDownloadRepository.findFileDownloadEntityById(downloadId);
            if(isAborted)
                download.setStatus(DownloadStatus.ABORTED);
            else
                download.setStatus(DownloadStatus.DOWNLOADED);
            fileDownloadRepository.save(download);
        };
    }

    @Transactional
    public void abort(Long downloadId, User user){
        FileDownloadEntity fileDownload = fileDownloadRepository.findFileDownloadEntityById(downloadId);
        fileDownloadExistenceRequiredValidation(fileDownload);
        fileDownloadUserValidation(fileDownload, user);

        DownloadStatus status = fileDownload.getStatus();
        if(status == DownloadStatus.NOT_STARTED)
            throw new ApiException("Download has not been started yet", HttpStatus.BAD_REQUEST);
        if(status == DownloadStatus.DOWNLOADED)
            throw new ApiException("Download has already been completed", HttpStatus.BAD_REQUEST);
        if(status == DownloadStatus.ABORTED)
            throw new ApiException("Download has already been aborted", HttpStatus.BAD_REQUEST);
        
        fileDownload.setStatus(DownloadStatus.ABORTED);
        fileDownloadRepository.save(fileDownload);
    }
}
