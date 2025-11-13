package com.example.campus.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 成绩组成项实体类
 */
@Data
public class GradeComponent {
    private Long componentId;
    private Long classId;
    private String componentName;
    private BigDecimal weight; // 权重，如0.3000代表30%
    private BigDecimal maxScore; // 该项满分
    private LocalDateTime createdAt;
}

