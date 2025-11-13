package com.example.campus.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 课程资源实体类
 */
@Data
public class CourseResource {
    private Long resourceId;
    private Long classId;
    private Long folderId;
    private Long uploaderId;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private String description;
    private LocalDateTime uploadAt;
    private Integer downloadCount;
}

