package com.example.campus.controller;

import com.example.campus.common.PageResult;
import com.example.campus.common.Result;
import com.example.campus.entity.Book;
import com.example.campus.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 书籍查询控制器 (管理员/学生)
 */
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookQueryController {
    private final BookService bookService;

    /**
     * 图书列表查询
     */
    @GetMapping
    public Result<PageResult<Book>> listBooks(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String tag_name,
            @RequestParam(required = false) Boolean is_admin_view) {
        PageResult<Book> result = bookService.listBooks(page, pageSize, title, author, tag_name, is_admin_view);
        return Result.success(result);
    }

    /**
     * 添加书籍标签
     */
    @PostMapping("/{book_id}/tags")
    public Result<Void> addBookTag(
            @PathVariable("book_id") Long bookId,
            @RequestBody BookTagRequest request) {
        bookService.addBookTag(bookId, request.getTagName());
        return Result.success();
    }

    @lombok.Data
    public static class BookTagRequest {
        private String tagName;
    }
}

