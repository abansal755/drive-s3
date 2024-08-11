package com.akshit.api.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.concurrent.atomic.AtomicReference;

@Data
@AllArgsConstructor
public class FileDownloadEntity {
    private final String id;
    private final Long userId;
    private final Long fileId;
    private final AtomicReference<DownloadStatus> status;
}
