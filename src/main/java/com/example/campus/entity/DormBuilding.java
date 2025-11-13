package com.example.campus.entity;

import lombok.Data;

/**
 * 宿舍楼实体类
 */
@Data
public class DormBuilding {
    private Integer buildingId;
    private String buildingName;
    private Integer genderLimit; // 1:男, 2:女, 0:不限
    private Long managerId;
    private Integer isActive; // 1:启用, 0:停用/维修
}

