package com.example.campus.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * AI教学建议实体类
 */
@Data
public class TeachingSuggestion {
    private Long suggestionId;
    private Long classId;
    private Long homeworkId;
    private String suggestionContent;
    private LocalDateTime generatedAt;
}

