package com.akshit.api.service;

import com.akshit.api.entity.UploadStatus;
import com.akshit.api.repo.FileRepository;
import com.akshit.api.repo.FileUploadRepository;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.InputStream;

@AllArgsConstructor
public class StreamCloserOnAbortThread extends Thread {

    private FileUploadRepository fileUploadRepository;
    private EntityManager entityManager;
    private InputStream inputStream;
    private Long fileUploadId;

    @Override
    public void run() {
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
    }
}
