package com.example.campus.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 考勤任务实体类
 */
@Data
public class AttendanceSession {
    private Long sessionId;
    private Long classId;
    private Long createdBy;
    private Integer sessionType; // 1:人脸, 2:定位, 3:人脸+定位
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal requiredLatitude;
    private BigDecimal requiredLongitude;
    private Integer requiredRadius;
    private Integer status; // 1:进行中, 2:已结束
}

