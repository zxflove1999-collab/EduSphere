package com.example.campus.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 宿舍房间概览视图
 */
@Data
public class DormRoomOverviewVO {
    @JsonProperty("room_id")
    private Long roomId;
    @JsonProperty("building_id")
    private Integer buildingId;
    @JsonProperty("building_name")
    private String buildingName;
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("room_number")
    private String roomNumber;
    @JsonProperty("total_beds")
    private Integer totalBeds;
    @JsonProperty("occupied_beds")
    private Integer occupiedBeds;
    @JsonProperty("occupancy_status")
    private Integer occupancyStatus; // 1:空闲, 2:部分入住, 3:住满
    @JsonProperty("is_full")
    private Integer isFull; // 1:满房, 0:未满
    @JsonProperty("room_type")
    private String roomType;
    @JsonProperty("has_private_bathroom")
    private Integer hasPrivateBathroom;
    @JsonProperty("current_occupants")
    private List<DormOccupantVO> currentOccupants;
}

