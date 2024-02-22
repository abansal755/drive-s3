package com.akshit.api.model;

import com.akshit.api.entity.UploadStatus;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadStatusResponse {
    private UploadStatus uploadStatus;
}
