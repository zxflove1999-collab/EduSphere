package com.example.campus.entity;

import lombok.Data;

/**
 * 书籍标签实体类
 */
@Data
public class Tag {
    private Integer tagId;
    private String tagName;
    private String description;
}

