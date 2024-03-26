package com.akshit.api.model;

import com.akshit.api.entity.FileEntity;
import com.akshit.api.entity.PermissionType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class File {
    private Long id;
    private String name;
    private String extension;
    private Long createdAt;
    private Long sizeInBytes;
    private PermissionType permissionType;

    public static File fromEntity(FileEntity file){
        return File
                .builder()
                .id(file.getId())
                .name(file.getName())
                .extension(file.getExtension())
                .createdAt(file.getCreatedAt())
                .sizeInBytes(file.getSizeInBytes())
                .build();
    }
}