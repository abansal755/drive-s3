package com.akshit.api.entity;

import lombok.*;

import java.util.concurrent.atomic.AtomicReference;

@Data
@Builder
@AllArgsConstructor
public class FileUploadEntity {
    private final String id;
    private final Long fileId;
    private final Long userId;
    private AtomicReference<UploadStatus> uploadStatus;
}