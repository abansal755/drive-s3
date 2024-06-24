package com.akshit.api.controller;

import com.akshit.api.model.DownloadInitiateResponse;
import com.akshit.api.model.User;
import com.akshit.api.service.FileDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/api/v1/downloads")
public class FileDownloadController {

    @Autowired
    private FileDownloadService fileDownloadService;

    @PostMapping("initiate/{fileId}")
    public DownloadInitiateResponse initiateDownload(@PathVariable Long fileId, @AuthenticationPrincipal User user){
        return fileDownloadService.initiateDownload(fileId, user);
    }

    @GetMapping("stream/{downloadId}")
    public StreamingResponseBody download(@PathVariable String downloadId, @AuthenticationPrincipal User user){
        return fileDownloadService.download(downloadId, user);
    }

    @PatchMapping("abort/{downloadId}")
    public void abort(@PathVariable String downloadId, @AuthenticationPrincipal User user){
        fileDownloadService.abort(downloadId, user);
    }
}
