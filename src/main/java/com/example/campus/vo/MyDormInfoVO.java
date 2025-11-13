package com.example.campus.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 我的宿舍信息视图
 */
@Data
public class MyDormInfoVO {
    @JsonProperty("room_id")
    private Long roomId;
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("bed_number")
    private String bedNumber;
    private List<DormOccupantVO> roommates;
    @JsonProperty("matching_score")
    private BigDecimal matchingScore;
}

