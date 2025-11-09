-- =========================================
-- 教室预约申请表
-- =========================================
CREATE TABLE IF NOT EXISTS t_reservation_request (
    request_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '预约申请ID',
    room_id BIGINT NOT NULL COMMENT '关联 t_classroom.room_id',
    requester_id BIGINT NOT NULL COMMENT '申请人ID (t_user.user_id)',
    start_time TIMESTAMP NOT NULL COMMENT '预约开始时间',
    end_time TIMESTAMP NOT NULL COMMENT '预约结束时间',
    purpose VARCHAR(255) NOT NULL COMMENT '预约用途',
    participants_count INT DEFAULT NULL COMMENT '参与人数',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 (1:待审批, 2:已批准, 3:已驳回, 4:已撤回)',
    approver_id BIGINT DEFAULT NULL COMMENT '审批人ID (t_user.user_id)',
    approval_remarks TEXT DEFAULT NULL COMMENT '审批意见',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (room_id) REFERENCES t_classroom(room_id),
    FOREIGN KEY (requester_id) REFERENCES t_user(user_id),
    FOREIGN KEY (approver_id) REFERENCES t_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教室预约申请表';

