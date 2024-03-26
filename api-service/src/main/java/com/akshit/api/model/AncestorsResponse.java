package com.akshit.api.model;

import com.akshit.api.entity.PermissionType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class AncestorsResponse {
    private List<Folder> ancestors;
    private User rootFolderOwner;
    private PermissionType permissionType;
}
