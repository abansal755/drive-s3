package com.akshit.api.model;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class FileUpdateRequest {
    private String name;
    private String extension;
}
