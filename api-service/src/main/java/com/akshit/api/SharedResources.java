package com.akshit.api;

import com.akshit.api.entity.FileDownloadEntity;
import com.akshit.api.entity.FileUploadEntity;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class SharedResources {

    public final ConcurrentMap<String, FileDownloadEntity> fileDownloads = new ConcurrentHashMap<>();

    public final ConcurrentMap<String, FileUploadEntity> fileUploads = new ConcurrentHashMap<>();
}
