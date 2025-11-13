package com.example.campus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 宿舍分配建议生成请求
 */
@Data
public class DormAllocationSuggestionRequest {
    @JsonProperty("target_dorm_building_id")
    private Integer targetDormBuildingId;
    @JsonProperty("matching_strategy")
    private Integer matchingStrategy;
}

