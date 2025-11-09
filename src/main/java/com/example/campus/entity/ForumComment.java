package com.example.campus.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 论坛评论实体类
 */
@Data
public class ForumComment {
    private Long commentId;
    private Long postId;
    private Long authorId;
    private Long parentCommentId; // 父评论ID (楼中楼)
    private String content;
    private Integer isDeleted; // 1:是, 0:否
    private LocalDateTime createdAt;
}

