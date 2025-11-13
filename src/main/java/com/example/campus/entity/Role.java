package com.example.campus.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色实体类
 */
@Data
public class Role {
    @JsonProperty("role_id")
    private Integer roleId;
    @JsonProperty("role_name")
    private String roleName;
    @JsonProperty("role_key")
    private String roleKey;
    private String description;
    @JsonProperty("is_system")
    private Integer isSystem; // 1:是, 0:否
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

