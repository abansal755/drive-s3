package com.akshit.api.controller;

import com.akshit.api.entity.FolderEntity;
import com.akshit.api.exception.ApiException;
import com.akshit.api.model.*;
import com.akshit.api.service.FolderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/folders")
public class FolderController {

    @Autowired
    private FolderService folderService;

    @GetMapping("root")
    public Folder getRootFolder(@AuthenticationPrincipal User user){
        return folderService.getRootFolderForUser(user);
    }

    @GetMapping("{folderId}")
    public FolderContentsResponse getFolderContents(
            @PathVariable Long folderId,
            @AuthenticationPrincipal User user)
    {
        return folderService.getFolderContents(folderId, user);
    }

    @PostMapping("")
    public Folder createFolder(
            @Valid @RequestBody FolderCreateRequest folderCreateRequest,
            @AuthenticationPrincipal User user)
    {
        return folderService.createFolder(folderCreateRequest, user);
    }

    @PatchMapping("{folderId}")
    public Folder updateFolder(
            @PathVariable Long folderId,
            @Valid @RequestBody FolderUpdateRequest folderUpdateRequest,
            @AuthenticationPrincipal User user)
    {
        return folderService.updateFolder(folderId, folderUpdateRequest, user);
    }

    @DeleteMapping("{folderId}")
    public void deleteFolder(@PathVariable Long folderId, @AuthenticationPrincipal User user) {
        folderService.deleteFolder(folderId, user);
    }
}
