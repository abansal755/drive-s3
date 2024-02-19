package com.akshit.api.controller;

import com.akshit.api.model.FolderContentsResponse;
import com.akshit.api.model.User;
import com.akshit.api.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/folders")
public class FolderController {

    @Autowired
    private FolderService folderService;

    @GetMapping("{folderId}")
    public ResponseEntity<FolderContentsResponse> getFolderContents(@PathVariable Long folderId, @AuthenticationPrincipal User user){
        return folderService.getFolderContents(folderId, user);
    }
}
