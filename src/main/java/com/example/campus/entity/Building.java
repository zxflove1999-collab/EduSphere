package com.example.campus.entity;

import lombok.Data;

/**
 * 教学楼实体类
 */
@Data
public class Building {
    private Integer buildingId;
    private String buildingName;
    private String location;
    private Integer isActive; // 1:启用, 0:停用
}

