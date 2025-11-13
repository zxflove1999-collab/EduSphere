package com.example.campus.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生选课实体类
 */
@Data
public class StudentClass {
    private Long id;
    private Long studentId;
    private Long classId;
    private LocalDateTime selectionTime;
    private Integer status; // 1:已选, 2:已退课
    private BigDecimal finalScore;
}

