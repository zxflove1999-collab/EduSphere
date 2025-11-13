package com.example.campus.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 教师资料实体类
 */
@Data
public class TeacherProfile {
    private Long userId;
    private String teacherIdNumber;
    private Integer gender; // 0:未知, 1:男, 2:女
    private String department;
    private String title;
    private String officeLocation;
    private String researchArea;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

