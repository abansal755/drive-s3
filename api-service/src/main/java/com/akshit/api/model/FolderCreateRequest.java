package com.akshit.api.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
public class FolderCreateRequest {

    @NotNull(message = "Folder name is required")
    private String folderName;

    @NotNull(message = "Parent folder ID is required")
    private Long parentFolderId;
}
