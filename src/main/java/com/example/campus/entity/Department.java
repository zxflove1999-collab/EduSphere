package com.example.campus.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 院系实体类
 */
@Data
public class Department {
    private Integer departmentId;
    private String departmentName;
    private String description;
    private LocalDateTime createdAt;
}

