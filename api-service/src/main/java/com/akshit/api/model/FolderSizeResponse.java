package com.akshit.api.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FolderSizeResponse {
    private Long sizeInBytes;
}
