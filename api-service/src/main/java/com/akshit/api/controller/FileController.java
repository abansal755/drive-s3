package com.akshit.api.controller;

import com.akshit.api.model.*;
import com.akshit.api.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("")
    public ResponseEntity<FileCreateResponse> createFile(@RequestBody FileCreateRequest fileCreateRequest, @AuthenticationPrincipal User user){
        return fileService.createFile(fileCreateRequest, user);
    }

    @GetMapping("{fileId}/download")
    public ResponseEntity<StreamingResponseBody> downloadFile(@PathVariable Long fileId, @AuthenticationPrincipal User user) throws IOException {
        return fileService.downloadFile(fileId, user);
    }

    @PatchMapping("{fileId}")
    public ResponseEntity<File> modifyFile(@RequestBody FileUpdateRequest fileUpdateRequest, @PathVariable Long fileId, @AuthenticationPrincipal User user){
        return fileService.modifyFile(fileUpdateRequest, fileId, user);
    }

    @DeleteMapping("{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId, @AuthenticationPrincipal User user){
        return fileService.deleteFile(fileId, user);
    }
}
