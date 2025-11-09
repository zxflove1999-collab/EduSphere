package com.example.campus.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 作业发布实体类
 */
@Data
public class Homework {
    private Long homeworkId;
    private Long classId;
    private Long createdBy;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Integer allowLateSubmission; // 1:是, 0:否
    private LocalDateTime createdAt;
}

