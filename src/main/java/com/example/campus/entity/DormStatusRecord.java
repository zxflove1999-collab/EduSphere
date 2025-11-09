package com.example.campus.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 宿舍状态记录实体类
 */
@Data
public class DormStatusRecord {
    private Long recordId;
    private Long studentId;
    private Integer statusType; // 1:在校, 2:离校, 3:晚归/夜不归宿
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String reason;
    private Long operatorId;
}

