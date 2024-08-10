package com.akshit.api.service;

import com.akshit.api.SharedResources;
import com.akshit.api.entity.DownloadStatus;
import com.akshit.api.entity.FileDownloadEntity;
import com.akshit.api.entity.FileEntity;
import com.akshit.api.entity.PermissionType;
import com.akshit.api.exception.ApiException;
import com.akshit.api.model.DownloadInitiateResponse;
import com.akshit.api.model.User;
import com.akshit.api.repo.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.UUID;

@Service
public class FileDownloadService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileHandlingService fileHandlingService;

    @Autowired
    private SharedResources sharedResources;

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
                .id(UUID.randomUUID().toString())
                .userId(user.getId())
                .fileId(fileId)
                .status(DownloadStatus.NOT_STARTED)
                .build();
        sharedResources.fileDownloads.put(fileDownload.getId(), fileDownload);
        return DownloadInitiateResponse
                .builder()
                .downloadId(fileDownload.getId())
                .build();
    }

    @Transactional(propagation = Propagation.NEVER)
    public StreamingResponseBody download(String downloadId, User user){
        FileDownloadEntity fileDownload = sharedResources.fileDownloads.get(downloadId);
        fileDownloadExistenceRequiredValidation(fileDownload);
        fileDownloadUserValidation(fileDownload, user);

        FileEntity file = fileRepository.findFileEntityById(fileDownload.getFileId());
        fileService.fileExistenceRequiredValidation(file);

        String fileName = file.getId().toString();
        fileDownload.setStatus(DownloadStatus.DOWNLOADING);

        return (OutputStream outputStream) -> {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            BufferedInputStream inputStream = fileHandlingService.getObject(fileName);

            while(true){
                if(fileDownload.getStatus() == DownloadStatus.ABORTED)
                    break;
                int b = inputStream.read();
                if(b == -1)
                    break;
                bufferedOutputStream.write(b);
            }
            bufferedOutputStream.flush();
            inputStream.close();

            sharedResources.fileDownloads.remove(downloadId);
        };
    }

    @Transactional
    public void abort(String downloadId, User user){
        FileDownloadEntity fileDownload = sharedResources.fileDownloads.get(downloadId);
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
    }
}
