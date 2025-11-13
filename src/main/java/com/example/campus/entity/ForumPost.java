package com.example.campus.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 论坛帖子实体类
 */
@Data
public class ForumPost {
    private Long postId;
    private Integer categoryId;
    private Long authorId;
    private String title;
    private String content;
    private Integer postStatus; // 1:正常, 2:已删除/隐藏
    private Integer isTop; // 1:是, 0:否
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long deletedBy;
}

