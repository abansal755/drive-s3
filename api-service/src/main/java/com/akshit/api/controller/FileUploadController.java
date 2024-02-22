package com.akshit.api.controller;

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
    public ResponseEntity<UploadStatusResponse> getUploadStatus(@PathVariable Long uploadId, @AuthenticationPrincipal User user){
        return fileUploadService.getUploadStatus(uploadId, user);
    }

    @PostMapping("{uploadId}")
    public ResponseEntity<Void> upload(HttpServletRequest request, @PathVariable Long uploadId, @AuthenticationPrincipal User user) throws IOException {
        return fileUploadService.upload(request, uploadId, user);
    }
}
