package com.akshit.api.model;

import com.akshit.api.entity.FileEntity;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class File {
    private Long id;
    private String name;
    private String extension;
    private Long createdAt;

    public static File fromEntity(FileEntity file){
        return File
                .builder()
                .id(file.getId())
                .name(file.getName())
                .extension(file.getExtension())
                .createdAt(file.getCreatedAt())
                .build();
    }
}