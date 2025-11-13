package com.example.campus.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 论坛评论实体类
 */
@Data
public class ForumComment {
    @JsonProperty("comment_id")
    private Long commentId;
    @JsonProperty("post_id")
    private Long postId;
    @JsonProperty("author_id")
    private Long authorId;
    @JsonProperty("parent_comment_id")
    private Long parentCommentId; // 父评论ID (楼中楼)
    private String content;
    @JsonProperty("is_deleted")
    private Integer isDeleted; // 1:是, 0:否
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}

