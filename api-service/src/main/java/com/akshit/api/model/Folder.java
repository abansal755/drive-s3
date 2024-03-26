package com.akshit.api.model;

import com.akshit.api.entity.FolderEntity;
import com.akshit.api.entity.PermissionType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class Folder {
    private Long id;
    private String folderName;
    private Long parentFolderId;
    private Long createdAt;
    private PermissionType permissionType;

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
