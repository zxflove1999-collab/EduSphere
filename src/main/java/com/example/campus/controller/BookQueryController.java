package com.example.campus.controller;

import com.example.campus.common.BusinessException;
import com.example.campus.common.PageResult;
import com.example.campus.common.Result;
import com.example.campus.entity.Book;
import com.example.campus.service.BookService;
import com.example.campus.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 书籍查询控制器 (管理员/学生)
 */
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookQueryController {
    private final BookService bookService;
    private final JwtUtil jwtUtil;

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
            @RequestParam(required = false) Boolean is_admin_view,
            HttpServletRequest request) {
        Boolean adminView = is_admin_view;
        if (adminView == null) {
            String token = request.getHeader("token");
            if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
                List<String> roles = jwtUtil.getRolesFromToken(token);
                adminView = !CollectionUtils.isEmpty(roles) && roles.stream().anyMatch(this::isAdminRole);
            } else {
                adminView = false;
            }
        }
        PageResult<Book> result = bookService.listBooks(page, pageSize, title, author, tag_name, adminView);
        return Result.success(result);
    }

    /**
     * 添加书籍标签
     */
    @PostMapping("/{book_id}/tags")
    public Result<Void> addBookTag(
            @PathVariable("book_id") Long bookId,
            @RequestBody BookTagRequest request) {
        validateTagRequest(request);
        bookService.addBookTag(bookId, request.getTagName().trim());
        return Result.success();
    }

    /**
     * 兼容接口文档中的单数形式
     */
    @PostMapping("/{book_id}/tag")
    public Result<Void> addBookTagAlias(@PathVariable("book_id") Long bookId,
                                        @RequestBody BookTagRequest request) {
        return addBookTag(bookId, request);
    }

    private boolean isAdminRole(String roleKey) {
        if (roleKey == null) {
            return false;
        }
        String normalized = roleKey.toLowerCase();
        return normalized.contains("admin") || normalized.contains("librarian");
    }

    private void validateTagRequest(BookTagRequest request) {
        if (request == null || !StringUtils.hasText(request.getTagName())) {
            throw new BusinessException("标签名称不能为空");
        }
        request.setTagName(request.getTagName().trim());
    }

    @lombok.Data
    public static class BookTagRequest {
        private String tagName;
    }
}

