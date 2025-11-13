package com.example.campus.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学生VO
 */
@Data
public class StudentVO {
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private Integer status;
    private String studentIdNumber;
    private Integer gender;
    private String major;
    private String className;
    private String grade;
    private LocalDate enrollmentDate;
    private LocalDateTime createdAt;
}

