package com.example.campus.entity;

import lombok.Data;

/**
 * 借阅策略实体类
 */
@Data
public class BookPolicy {
    private Integer policyId;
    private String policyName;
    private Integer maxDurationDays;
    private Integer maxBorrowCount;
    private Integer allowRenewalCount;
    private Integer userTypeScope; // 1:学生, 2:教师, NULL:所有
}

