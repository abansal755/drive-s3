package com.akshit.api.entity;

import lombok.*;

import java.util.concurrent.atomic.AtomicReference;

@Data
@AllArgsConstructor
public class FileUploadEntity {
    private final String id;
    private final Long fileId;
    private final Long userId;
    private final AtomicReference<UploadStatus> uploadStatus;
}