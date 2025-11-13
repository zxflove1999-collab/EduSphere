package com.example.campus.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 请假申请实体类
 */
@Data
public class LeaveRequest {
    private Long requestId;
    private Long studentId;
    private Long classId;
    private String reason;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status; // 1:待审批, 2:已批准, 3:已驳回
    private Long approverId;
    private String approvalRemarks;
    private LocalDateTime submittedAt;
}

