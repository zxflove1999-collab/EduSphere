package com.example.campus.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 角色实体类
 */
@Data
public class Role {
    private Integer roleId;
    private String roleName;
    private String roleKey;
    private String description;
    private Integer isSystem; // 1:是, 0:否
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

