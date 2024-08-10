package com.akshit.api.service;

import java.io.BufferedInputStream;
import java.io.InputStream;

public interface FileHandlingService {
    void putObject(String objectKey, InputStream inputStream, long contentLength);
    BufferedInputStream getObject(String objectKey);
    void deleteObject(String objectKey);
    Long getObjectSize(String objectKey);
}
