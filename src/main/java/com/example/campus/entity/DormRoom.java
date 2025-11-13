package com.example.campus.entity;

import lombok.Data;

/**
 * 宿舍房间实体类
 */
@Data
public class DormRoom {
    private Long roomId;
    private Integer buildingId;
    private String roomNumber;
    private String fullName;
    private Integer totalBeds;
    private Integer occupiedBeds;
    private Integer roomStatus; // 1:正常, 2:维修, 3:已满
}

