package com.akshit.api.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
public class FolderCreateRequest {
    private String folderName;
    private Long parentFolderId;
}
