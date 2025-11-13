package com.example.campus.service;

import com.example.campus.common.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件上传服务
 */
@Service
@RequiredArgsConstructor
public class UploadService {
    @Value("${file.upload.path}")
    private String uploadPath;

    /**
     * 上传文件
     */
    public String uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        try {
            // 创建上传目录
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                    + UUID.randomUUID().toString().substring(0, 8) + extension;

            // 保存文件
            Path filePath = Paths.get(uploadPath, fileName);
            Files.write(filePath, file.getBytes());

            // 返回文件URL（这里简化处理，实际应该返回完整的URL）
            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new BusinessException("文件上传失败：" + e.getMessage());
        }
    }
}

