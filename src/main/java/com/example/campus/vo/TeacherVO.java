package com.example.campus.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 教师VO
 */
@Data
public class TeacherVO {
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private Integer status;
    private String teacherIdNumber;
    private Integer gender;
    private String department;
    private String title;
    private String officeLocation;
    private LocalDateTime createdAt;
}

