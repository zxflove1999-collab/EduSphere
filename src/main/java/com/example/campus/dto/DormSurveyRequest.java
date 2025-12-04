package com.example.campus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 宿舍问卷请求体
 * 包含基础偏好、详细问卷答案以及用于AI分析的自我介绍
 */
@Data
public class DormSurveyRequest {
    // --- 基础时间字段 ---
    @JsonProperty("wake_up_time")
    private String wakeUpTime;
    
    @JsonProperty("sleep_time")
    private String sleepTime;

    // --- 基础偏好字段 (旧版兼容) ---
    @JsonProperty("cleanliness_preference")
    private Integer cleanlinessPreference;
    
    @JsonProperty("noise_tolerance")
    private Integer noiseTolerance;

    // --- 详细问卷字段 (新版 18 题) ---
    @JsonProperty("sleep_time_pref")
    private Integer sleepTimePref;       // 1. 睡觉时间偏好
    
    @JsonProperty("wake_time_pref")
    private Integer wakeTimePref;        // 2. 起床时间偏好
    
    @JsonProperty("nap_habit")
    private Integer napHabit;            // 3. 午睡习惯
    
    @JsonProperty("smoking_status")
    private Integer smokingStatus;       // 4. 抽烟习惯
    
    @JsonProperty("roommate_smoke_accept")
    private Integer roommateSmokeAccept; // 5. 室友抽烟接受度
    
    @JsonProperty("gaming_freq")
    private Integer gamingFreq;          // 6. 游戏频率
    
    @JsonProperty("headphone_usage")
    private Integer headphoneUsage;      // 7. 耳机使用习惯
    
    @JsonProperty("chatting_pref")
    private Integer chattingPref;        // 8. 聊天偏好
    
    @JsonProperty("guest_acceptance")
    private Integer guestAcceptance;     // 9. 带客接受度
    
    @JsonProperty("group_activity_willingness")
    private Integer groupActivityWillingness; // 10. 集体活动意愿
    
    @JsonProperty("hygiene_requirement")
    private Integer hygieneRequirement;  // 11. 卫生要求
    
    @JsonProperty("organization_level")
    private Integer organizationLevel;   // 12. 整理程度
    
    @JsonProperty("roommate_hygiene_tolerance")
    private Integer roommateHygieneTolerance; // 13. 室友卫生容忍度
    
    @JsonProperty("quiet_study_need")
    private Integer quietStudyNeed;      // 14. 安静学习需求
    
    @JsonProperty("noise_tolerance_level")
    private Integer noiseToleranceLevel; // 15. 噪音容忍等级
    
    @JsonProperty("dorm_food_freq")
    private Integer dormFoodFreq;        // 16. 宿舍饮食频率
    
    @JsonProperty("fragrance_usage")
    private Integer fragranceUsage;      // 17. 香氛使用
    
    @JsonProperty("smell_sensitivity")
    private Integer smellSensitivity;    // 18. 气味敏感度

    // --- AI 分析字段 ---
    // 🔥 关键字段：接收前端传来的“特殊需求/自我介绍”文本
    @JsonProperty("self_introduction")
    private String selfIntroduction;
}