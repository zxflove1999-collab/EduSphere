package com.example.campus.controller;

import com.example.campus.common.Result;
import com.example.campus.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController {
    private final UploadService uploadService;

    /**
     * 文件上传
     */
    @PostMapping
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        String fileUrl = uploadService.uploadFile(file);
        return Result.success(fileUrl);
    }
}

