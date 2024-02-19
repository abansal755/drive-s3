package com.akshit.api.model;

import com.akshit.api.entity.FolderEntity;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Folder {
    private Long id;
    private String folderName;
    private Long parentFolderId;
    private Long createdAt;

    public static Folder fromEntity(FolderEntity folder){
        return Folder
                .builder()
                .id(folder.getId())
                .folderName(folder.getFolderName())
                .parentFolderId(folder.getParentFolderId())
                .createdAt(folder.getCreatedAt())
                .build();
    }
}
