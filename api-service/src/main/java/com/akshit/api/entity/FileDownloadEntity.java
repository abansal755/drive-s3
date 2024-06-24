package com.akshit.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileDownloadEntity {
    private String id;
    private Long userId;
    private Long fileId;
    private DownloadStatus status;
}
