package com.example.campus.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生考勤记录实体类
 */
@Data
public class AttendanceRecord {
    private Long recordId;
    private Long sessionId;
    private Long studentId;
    private Integer status; // 1:出勤, 2:缺勤, 3:迟到, 4:请假
    private LocalDateTime checkInTime;
    private BigDecimal checkInLatitude;
    private BigDecimal checkInLongitude;
    private BigDecimal faceApiScore;
    private String remarks;
    private Long updatedBy;
}

