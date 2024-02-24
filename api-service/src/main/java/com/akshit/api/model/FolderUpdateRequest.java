package com.akshit.api.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class FolderUpdateRequest {

    @NotNull(message = "Folder name is required")
    private String folderName;
}
