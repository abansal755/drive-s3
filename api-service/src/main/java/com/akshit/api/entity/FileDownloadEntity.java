package com.akshit.api.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FileDownloadEntity {
    private final String id;
    private final Long userId;
    private final Long fileId;
    private volatile DownloadStatus status;
}
