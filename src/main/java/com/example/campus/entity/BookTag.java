package com.example.campus.entity;

import lombok.Data;

/**
 * 书籍与标签关联实体类
 */
@Data
public class BookTag {
    private Long id;
    private Long bookId;
    private Integer tagId;
}

