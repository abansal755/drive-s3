package com.akshit.api.controller;

import com.akshit.api.exception.ApiException;
import com.akshit.api.model.UploadStatusResponse;
import com.akshit.api.model.User;
import com.akshit.api.service.FileUploadService;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/uploads")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @GetMapping("{uploadId}")
    public UploadStatusResponse getUploadStatus(
            @PathVariable Long uploadId,
            @AuthenticationPrincipal User user) throws ApiException
    {
        return fileUploadService.getUploadStatus(uploadId, user);
    }

    @PutMapping("{uploadId}")
    public void upload(
            HttpServletRequest request,
            @PathVariable Long uploadId,
            @AuthenticationPrincipal User user) throws IOException, ApiException
    {
        fileUploadService.upload(request, uploadId, user);
    }
}
