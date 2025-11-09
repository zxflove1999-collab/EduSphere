package com.example.campus.entity;

import lombok.Data;

/**
 * 论坛板块实体类
 */
@Data
public class ForumCategory {
    private Integer categoryId;
    private String categoryName;
    private String description;
    private Integer isActive; // 1:启用, 0:停用
}

