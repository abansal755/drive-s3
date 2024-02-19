package com.akshit.api.model;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderContentsResponse {
    List<File> files;
    List<Folder> folders;
}
