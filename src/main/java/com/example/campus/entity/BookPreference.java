package com.example.campus.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 书籍偏好/推荐实体类
 */
@Data
public class BookPreference {
    private Long id;
    private Long userId;
    private Long recommendedBookId;
    private String reason;
    private BigDecimal score;
    private LocalDateTime generatedAt;
}

