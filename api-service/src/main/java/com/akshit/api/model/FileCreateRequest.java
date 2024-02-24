package com.akshit.api.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class FileCreateRequest {

    @NotNull(message = "Parent folder ID is required")
    private Long parentFolderId;

    @NotNull(message = "File name is required")
    private String name;

    @NotNull(message = "File extension is required")
    private String extension;
}
