package com.example.campus.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 作业附件实体类
 */
@Data
public class SubmissionFile {
    private Long fileId;
    private Long submissionId;
    private String fileName;
    private String fileUrl;
    private Long fileSize;
    private LocalDateTime uploadedAt;
}

