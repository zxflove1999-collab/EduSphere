package com.example.campus.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 论坛板块摘要视图
 */
@Data
public class ForumCategorySummaryVO {
    @JsonProperty("category_id")
    private Integer categoryId;
    @JsonProperty("category_name")
    private String categoryName;
    @JsonProperty("post_count")
    private Long postCount;
}

