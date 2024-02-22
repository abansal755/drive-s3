package com.akshit.api.model;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class FileCreateRequest {
    private Long parentFolderId;
    private String name;
    private String extension;
}
