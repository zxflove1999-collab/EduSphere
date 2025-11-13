package com.example.campus.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 宿舍住户信息视图
 */
@Data
public class DormOccupantVO {
    @JsonProperty("student_id")
    private Long studentId;
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonProperty("bed_number")
    private String bedNumber;
}

