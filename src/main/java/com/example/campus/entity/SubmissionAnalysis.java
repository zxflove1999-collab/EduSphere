package com.example.campus.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * AI作业分析实体类
 */
@Data
public class SubmissionAnalysis {
    private Long analysisId;
    private Long submissionId;
    private String studentAcReport; // JSON格式
    private Integer analysisStatus; // 1:待分析, 2:分析中, 3:已完成, 4:失败
    private LocalDateTime generatedAt;
}

