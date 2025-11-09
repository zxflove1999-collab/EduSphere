package com.example.campus.controller;

import com.example.campus.common.Result;
import com.example.campus.entity.CourseResource;
import com.example.campus.service.ResourceService;
import com.example.campus.service.UploadService;
import com.example.campus.util.JwtUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 课程资源管理控制器
 */
@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;
    private final UploadService uploadService;
    private final JwtUtil jwtUtil;

    /**
     * 查询班级资源列表
     */
    @GetMapping("/{class_id}/resources")
    public Result<Map<String, Object>> listResources(
            @PathVariable("class_id") Long classId,
            @RequestParam(required = false) Long folder_id) {
        Map<String, Object> result = resourceService.listResources(classId, folder_id);
        return Result.success(result);
    }

    /**
     * 创建资源文件夹
     */
    @PostMapping("/{class_id}/resources/folders")
    public Result<Void> createFolder(
            @PathVariable("class_id") Long classId,
            @RequestBody FolderCreateRequest request,
            HttpServletRequest httpRequest) {
        Long userId = jwtUtil.getUserIdFromToken(httpRequest.getHeader("token"));
        resourceService.createFolder(classId, request.getFolder_name(), request.getParent_folder_id(), userId);
        return Result.success();
    }

    /**
     * 上传课程资源
     */
    @PostMapping("/{class_id}/resources/upload")
    public Result<CourseResource> uploadResource(
            @PathVariable("class_id") Long classId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Long folder_id,
            @RequestParam(required = false) String description,
            HttpServletRequest httpRequest) {
        Long userId = jwtUtil.getUserIdFromToken(httpRequest.getHeader("token"));
        
        // 上传文件
        String fileUrl = uploadService.uploadFile(file);
        
        // 创建资源记录
        CourseResource resource = resourceService.uploadResource(
            classId, folder_id, userId,
            file.getOriginalFilename(), fileUrl,
            getFileType(file.getOriginalFilename()),
            file.getSize(), description
        );
        
        return Result.success(resource);
    }

    /**
     * 删除课程资源
     */
    @DeleteMapping("/resources/{resource_id}")
    public Result<Void> deleteResource(@PathVariable("resource_id") Long resourceId) {
        resourceService.deleteResource(resourceId);
        return Result.success();
    }

    /**
     * 删除资源文件夹
     */
    @DeleteMapping("/resources/folders/{folder_id}")
    public Result<Void> deleteFolder(@PathVariable("folder_id") Long folderId) {
        resourceService.deleteFolder(folderId);
        return Result.success();
    }

    /**
     * 下载课程资源 (增加下载次数)
     */
    @GetMapping("/resources/{resource_id}/download")
    public Result<String> downloadResource(@PathVariable("resource_id") Long resourceId) {
        CourseResource resource = resourceService.getResourceById(resourceId);
        if (resource == null) {
            return Result.error("资源不存在");
        }
        // 增加下载次数
        resourceService.incrementDownloadCount(resourceId);
        return Result.success(resource.getFileUrl());
    }

    private String getFileType(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    @Data
    public static class FolderCreateRequest {
        private String folder_name;
        private Long parent_folder_id;
    }
}

