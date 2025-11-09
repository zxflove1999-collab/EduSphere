package com.example.campus.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 书籍信息实体类
 */
@Data
public class Book {
    private Long bookId;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private Integer publicationYear;
    private Integer totalCopies;
    private Integer availableCopies;
    private String coverUrl;
    private String summary;
    private Long uploaderId;
    private LocalDateTime uploadedAt;
}

