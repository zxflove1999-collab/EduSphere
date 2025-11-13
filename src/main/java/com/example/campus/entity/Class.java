package com.example.campus.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 开课班级实体类
 */
@Data
public class Class {
    private Long classId;
    private Long courseId;
    private Long teacherId;
    private String semester;
    private Integer maxCapacity;
    private Integer currentEnrollment;
    private Integer status; // 1:待开放, 2:选课中, 3:授课中, 4:已结课
    private LocalDateTime createdAt;
}

