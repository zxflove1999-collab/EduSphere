package com.example.campus.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生单项成绩实体类
 */
@Data
public class StudentGrade {
    private Long gradeId;
    private Long studentId;
    private Long componentId;
    private BigDecimal score;
    private Long recordedBy;
    private LocalDateTime recordedAt;
}

