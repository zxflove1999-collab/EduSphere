package com.example.campus.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程模板实体类
 */
@Data
public class Course {
    private Long courseId;
    private String courseCode;
    private String courseName;
    private Integer departmentId;
    private BigDecimal credits;
    private String description;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

