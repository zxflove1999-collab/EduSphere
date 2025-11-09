package com.example.campus.controller;

import com.example.campus.common.Result;
import com.example.campus.entity.Book;
import com.example.campus.service.BookService;
import com.example.campus.util.JwtUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 书籍管理控制器
 */
@RestController
@RequestMapping("/admin/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final JwtUtil jwtUtil;

    /**
     * 上传书籍
     */
    @PostMapping
    public Result<Void> uploadBook(
            @RequestBody BookCreateRequest request,
            HttpServletRequest httpRequest) {
        Long uploaderId = jwtUtil.getUserIdFromToken(httpRequest.getHeader("token"));
        Book book = new Book();
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setPublicationYear(request.getPublication_year());
        book.setTotalCopies(request.getTotal_copies());
        book.setCoverUrl(request.getCover_url());
        book.setSummary(request.getSummary());
        bookService.uploadBook(book, uploaderId, request.getTags());
        return Result.success();
    }

    @Data
    public static class BookCreateRequest {
        private String isbn;
        private String title;
        private String author;
        private String publisher;
        private Integer publication_year;
        private Integer total_copies;
        private String cover_url;
        private String summary;
        private List<String> tags;
    }
}

