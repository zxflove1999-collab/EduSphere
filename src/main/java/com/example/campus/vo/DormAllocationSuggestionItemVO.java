package com.example.campus.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 宿舍分配建议项视图
 */
@Data
public class DormAllocationSuggestionItemVO {
    @JsonProperty("student_id")
    private Long studentId;
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("suggested_room")
    private String suggestedRoom;
    @JsonProperty("suggested_bed_id")
    private Long suggestedBedId;
    @JsonProperty("average_match_score")
    private BigDecimal averageMatchScore;
}

