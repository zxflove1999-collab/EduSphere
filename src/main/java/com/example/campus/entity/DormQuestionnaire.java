package com.example.campus.entity;

import lombok.Data;

/**
 * 存储用户提交的 18 道宿舍问卷 + 补充说明
 */
@Data
public class DormQuestionnaire {

    private Long id;
    private Long studentId;

    private Integer q1SleepTime;
    private Integer q2WakeTime;
    private Integer q3Nap;
    private Integer q4Smoke;
    private Integer q5MindSmoke;
    private Integer q6Game;
    private Integer q7Headphone;
    private Integer q8Chat;
    private Integer q9FriendVisit;
    private Integer q10Activity;
    private Integer q11CleanDemand;
    private Integer q12Tidy;
    private Integer q13MindDirty;
    private Integer q14QuietStudy;
    private Integer q15NoiseTolerance;
    private Integer q16EatInDorm;
    private Integer q17Perfume;
    private Integer q18MindSmell;

    private String extraNote;  // 补充说明（自由文本）
}
