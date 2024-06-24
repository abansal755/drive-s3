package com.akshit.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadEntity {
    private String id;
    private Long fileId;
    private Long userId;
    private UploadStatus uploadStatus;
}