package com.example.campus.vo;

import lombok.Data;

/**
 * 角色VO
 */
@Data
public class RoleVO {
    private Integer roleId;
    private String roleName;
    private String roleKey;
    private String description;
    private Integer isSystem;
    private String createdAt;
}

