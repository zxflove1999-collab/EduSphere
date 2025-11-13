package com.example.campus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 宿舍偏好问卷请求
 */
@Data
public class DormPreferenceRequest {
    @JsonProperty("wake_up_time")
    private String wakeUpTime;
    private String bedtime;
    @JsonProperty("study_habit_preference")
    private Integer studyHabitPreference;
    @JsonProperty("hygiene_reference")
    private Integer hygienePreference;
    @JsonProperty("noise_tolerance")
    private Integer noiseTolerance;
    @JsonProperty("self_introduction")
    private String selfIntroduction;
}

