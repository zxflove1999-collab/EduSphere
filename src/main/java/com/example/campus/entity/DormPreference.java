package com.example.campus.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 宿舍偏好问卷实体
 */
@Data
public class DormPreference {
    private Long preferenceId;
    private Long studentId;
    private LocalTime wakeUpTime;
    private LocalTime bedtime;
    private Integer studyHabitPreference;
    private Integer hygienePreference;
    private Integer noiseTolerance;
    private String selfIntroduction;
    private LocalDateTime submissionTime;
    private BigDecimal matchingScore;
}

