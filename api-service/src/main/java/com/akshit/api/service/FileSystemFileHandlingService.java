package com.akshit.api.service;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class FileSystemFileHandlingService implements FileHandlingService {

    private final String objectsPath;

    public FileSystemFileHandlingService(@Value("${files-dir}") String objectsPath) {
        this.objectsPath = objectsPath;
    }

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(Paths.get(objectsPath));
    }

    @Override
    public void putObject(String objectKey, InputStream inputStream, long contentLength) {
        try {
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(getFilePath(objectKey)));
            int b;
            while(true){
                b = inputStream.read();
                if(b == -1) break;
                outputStream.write(b);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BufferedInputStream getObject(String objectKey) {
        try {
            return new BufferedInputStream(new FileInputStream(getFilePath(objectKey)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteObject(String objectKey) {
        try {
            Files.deleteIfExists(Paths.get(getFilePath(objectKey)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long getObjectSize(String objectKey) {
        try {
            return Files.size(Paths.get(getFilePath(objectKey)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFilePath(String objectKey) {
        return Paths.get(objectsPath, objectKey).toString();
    }
}
