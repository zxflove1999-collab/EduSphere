package com.example.campus.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * 宿舍问卷实体类
 */
@Data
public class DormSurvey {
    private Long surveyId;
    private Long studentId;
    private LocalTime wakeUpTime;
    private LocalTime sleepTime;
    private Integer cleanlinessPreference; // 1:严格, 2:一般, 3:随意
    private Integer noiseTolerance;
    private LocalDateTime submissionTime;
    private BigDecimal matchingScore;
}

