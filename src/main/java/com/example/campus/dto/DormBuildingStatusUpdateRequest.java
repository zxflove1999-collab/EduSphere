package com.example.campus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 宿舍楼状态更新请求
 */
@Data
public class DormBuildingStatusUpdateRequest {
    @JsonProperty("is_active")
    private Integer isActive;
    private String reason;
}

