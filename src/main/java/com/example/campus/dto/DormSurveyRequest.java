package com.example.campus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 宿舍问卷（t_dorm_survey）请求体
 */
@Data
public class DormSurveyRequest {
    @JsonProperty("wake_up_time")
    private String wakeUpTime;
    @JsonProperty("sleep_time")
    private String sleepTime;
    @JsonProperty("cleanliness_preference")
    private Integer cleanlinessPreference;
    @JsonProperty("noise_tolerance")
    private Integer noiseTolerance;
    @JsonProperty("sleep_time_pref")
    private Integer sleepTimePref;
    @JsonProperty("wake_time_pref")
    private Integer wakeTimePref;
    @JsonProperty("nap_habit")
    private Integer napHabit;
    @JsonProperty("smoking_status")
    private Integer smokingStatus;
    @JsonProperty("roommate_smoke_accept")
    private Integer roommateSmokeAccept;
    @JsonProperty("gaming_freq")
    private Integer gamingFreq;
    @JsonProperty("headphone_usage")
    private Integer headphoneUsage;
    @JsonProperty("chatting_pref")
    private Integer chattingPref;
    @JsonProperty("guest_acceptance")
    private Integer guestAcceptance;
    @JsonProperty("group_activity_willingness")
    private Integer groupActivityWillingness;
    @JsonProperty("hygiene_requirement")
    private Integer hygieneRequirement;
    @JsonProperty("organization_level")
    private Integer organizationLevel;
    @JsonProperty("roommate_hygiene_tolerance")
    private Integer roommateHygieneTolerance;
    @JsonProperty("quiet_study_need")
    private Integer quietStudyNeed;
    @JsonProperty("noise_tolerance_level")
    private Integer noiseToleranceLevel;
    @JsonProperty("dorm_food_freq")
    private Integer dormFoodFreq;
    @JsonProperty("fragrance_usage")
    private Integer fragranceUsage;
    @JsonProperty("smell_sensitivity")
    private Integer smellSensitivity;
}

