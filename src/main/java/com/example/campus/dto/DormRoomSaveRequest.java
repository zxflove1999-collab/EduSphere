package com.example.campus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 宿舍房间新增/修改请求
 */
@Data
public class DormRoomSaveRequest {
    @JsonProperty("room_id")
    private Long roomId;
    @JsonProperty("building_id")
    private Integer buildingId;
    @JsonProperty("room_number")
    private String roomNumber;
    @JsonProperty("total_beds")
    private Integer totalBeds;
    @JsonProperty("room_type")
    private String roomType;
    @JsonProperty("has_private_bathroom")
    private Integer hasPrivateBathroom;
}

