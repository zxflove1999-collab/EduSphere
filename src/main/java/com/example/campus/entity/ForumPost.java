package com.example.campus.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 论坛帖子实体类
 */
@Data
public class ForumPost {
    @JsonProperty("post_id")
    private Long postId;
    @JsonProperty("category_id")
    private Integer categoryId;
    @JsonProperty("author_id")
    private Long authorId;
    private String title;
    private String content;
    @JsonProperty("post_status")
    private Integer postStatus; // 1:正常, 2:已删除/隐藏
    @JsonProperty("is_top")
    private Integer isTop; // 1:是, 0:否
    @JsonProperty("view_count")
    private Integer viewCount;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    @JsonProperty("deleted_by")
    private Long deletedBy;
}

