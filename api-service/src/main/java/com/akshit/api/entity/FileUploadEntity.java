package com.akshit.api.entity;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class FileUploadEntity {
    private final String id;
    private final Long fileId;
    private final Long userId;
    private volatile UploadStatus uploadStatus;
}