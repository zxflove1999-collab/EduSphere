package com.example.campus.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 教室预约申请实体类
 */
@Data
public class ReservationRequest {
    private Long requestId;
    private Long roomId;
    private Long requesterId; // 申请人ID (t_user.user_id)
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String purpose;
    private Integer participantsCount;
    private Integer status; // 1:待审批, 2:已批准, 3:已驳回, 4:已撤回
    private Long approverId; // 审批人ID (t_user.user_id)
    private String approvalRemarks; // 审批意见
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

