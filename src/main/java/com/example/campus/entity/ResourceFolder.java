package com.example.campus.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 资源文件夹实体类
 */
@Data
public class ResourceFolder {
    private Long folderId;
    private Long classId;
    private String folderName;
    private Long parentFolderId;
    private Long createdBy;
    private LocalDateTime createdAt;
}

