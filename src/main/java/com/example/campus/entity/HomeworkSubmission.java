package com.example.campus.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 作业提交实体类
 */
@Data
public class HomeworkSubmission {
    private Long submissionId;
    private Long homeworkId;
    private Long studentId;
    private String content;
    private LocalDateTime submittedAt;
    private Integer status; // 1:准时提交, 2:迟交, 3:未提交
    private BigDecimal grade;
    private String teacherFeedback;
}

