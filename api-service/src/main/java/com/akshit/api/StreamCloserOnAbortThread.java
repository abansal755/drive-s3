package com.akshit.api;

import com.akshit.api.entity.FileUploadEntity;
import com.akshit.api.entity.UploadStatus;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.InputStream;

@AllArgsConstructor
public class StreamCloserOnAbortThread extends Thread {

    private final FileUploadEntity fileUploadEntity;
    private final InputStream inputStream;

    @Override
    public void run() {
        try {
            while(!Thread.interrupted()){
                if(fileUploadEntity.getUploadStatus() == UploadStatus.ABORTED){
                    inputStream.close();
                    break;
                }
            }
        }
        catch (IOException ex){
            System.err.println(ex);
        }
    }
}
