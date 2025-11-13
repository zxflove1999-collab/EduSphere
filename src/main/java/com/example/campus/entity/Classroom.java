package com.example.campus.entity;

import lombok.Data;
import java.time.LocalDate;

/**
 * 教室信息实体类
 */
@Data
public class Classroom {
    private Long roomId;
    private String roomNumber;
    private Integer buildingId;
    private String fullName;
    private Integer capacity;
    private String roomType;
    private String equipment; // JSON字符串或逗号分隔的字符串
    private Integer isAvailable; // 1:可用, 0:停用/维修
    private LocalDate lastMaintenanceDate;
}

