package com.example.campus.entity;

import lombok.Data;

/**
 * 用户角色关联实体类
 */
@Data
public class UserRole {
    private Long id;
    private Long userId;
    private Integer roleId;
}

