package com.akshit.api.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class TempStorageService {

    @Value("${temp-file-storage}")
    private String TEMP_FILE_STORAGE_DIR;

    @PostConstruct
    private void init() throws IOException {
        createDirectoryHierarchyIfNotExists(TEMP_FILE_STORAGE_DIR);
    }

    private void createDirectoryHierarchyIfNotExists(String path) throws IOException {
        Files.createDirectories(Paths.get(path));
    }

    public void deleteFileIfExists(String fileName) throws IOException {
        Files.deleteIfExists(Paths.get(getPath(fileName)));
    }

    public void downloadStreamToFile(BufferedInputStream inputStream, String filename) throws IOException {
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(getPath(filename)));
        int b;
        while((b = inputStream.read()) != -1)
            outputStream.write(b);
        outputStream.flush();
        outputStream.close();
    }

    public String getPath(String fileName){
        return TEMP_FILE_STORAGE_DIR + File.separator + fileName;
    }
}
