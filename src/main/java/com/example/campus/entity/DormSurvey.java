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
    private Integer sleepTimePref;
    private Integer wakeTimePref;
    private Integer napHabit;
    private Integer smokingStatus;
    private Integer roommateSmokeAccept;
    private Integer gamingFreq;
    private Integer headphoneUsage;
    private Integer chattingPref;
    private Integer guestAcceptance;
    private Integer groupActivityWillingness;
    private Integer hygieneRequirement;
    private Integer organizationLevel;
    private Integer roommateHygieneTolerance;
    private Integer quietStudyNeed;
    private Integer noiseToleranceLevel;
    private Integer dormFoodFreq;
    private Integer fragranceUsage;
    private Integer smellSensitivity;
    private LocalDateTime submissionTime;
    private BigDecimal matchingScore;
}

