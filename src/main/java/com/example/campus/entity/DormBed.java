package com.example.campus.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 宿舍床位实体类
 */
@Data
public class DormBed {
    private Long bedId;
    private Long roomId;
    private String bedNumber;
    private Integer bedStatus; // 1:空闲, 2:已分配, 3:禁用
    private Long currentResidentId;
    private LocalDateTime allocationTime;
}

