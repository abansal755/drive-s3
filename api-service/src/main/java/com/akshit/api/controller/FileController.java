package com.akshit.api.controller;

import com.akshit.api.exception.ApiException;
import com.akshit.api.model.*;
import com.akshit.api.service.FileService;
import jakarta.validation.Valid;
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
    public FileCreateResponse createFile(
            @Valid @RequestBody FileCreateRequest fileCreateRequest,
            @AuthenticationPrincipal User user)
    {
        return fileService.createFile(fileCreateRequest, user);
    }

    @GetMapping("{fileId}/download")
    public StreamingResponseBody downloadFile(
            @PathVariable Long fileId,
            @AuthenticationPrincipal User user) throws IOException
    {
        return fileService.downloadFile(fileId, user);
    }

    @PatchMapping("{fileId}")
    public File modifyFile(
            @RequestBody FileUpdateRequest fileUpdateRequest,
            @PathVariable Long fileId,
            @AuthenticationPrincipal User user)
    {
        return fileService.modifyFile(fileUpdateRequest, fileId, user);
    }

    @DeleteMapping("{fileId}")
    public void deleteFile(
            @PathVariable Long fileId,
            @AuthenticationPrincipal User user)
    {
        fileService.deleteFile(fileId, user);
    }
}
