package com.example.campus.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学生资料实体类
 */
@Data
public class StudentProfile {
    private Long userId;
    private String studentIdNumber;
    private Integer gender; // 0:未知, 1:男, 2:女
    private LocalDate dateOfBirth;
    private String major;
    private String className;
    private String grade;
    private LocalDate enrollmentDate;
    private Long currentDormId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

