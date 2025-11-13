package com.example.campus.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 论坛板块实体类
 */
@Data
public class ForumCategory {
    @JsonProperty("category_id")
    private Integer categoryId;
    @JsonProperty("category_name")
    private String categoryName;
    private String description;
    @JsonProperty("is_active")
    private Integer isActive; // 1:启用, 0:停用
}

