package com.example.campus.controller;

import com.example.campus.common.PageResult;
import com.example.campus.common.Result;
import com.example.campus.entity.BookPreference;
import com.example.campus.entity.BorrowRecord;
import com.example.campus.service.BorrowService;
import com.example.campus.util.JwtUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 图书借阅控制器
 */
@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class BorrowController {
    private final BorrowService borrowService;
    private final JwtUtil jwtUtil;

    /**
     * 借阅书籍
     */
    @PostMapping("/borrow")
    public Result<Void> borrowBook(
            @RequestBody BorrowRequest request,
            HttpServletRequest httpRequest) {
        Long borrowerId = jwtUtil.getUserIdFromToken(httpRequest.getHeader("token"));
        // 获取用户类型（需要从JWT或用户信息中获取）
        // 这里暂时简化处理，假设是学生（1）
        Integer userTypeScope = 1;
        borrowService.borrowBook(request.getBook_id(), borrowerId, userTypeScope);
        return Result.success();
    }

    /**
     * 我的借阅记录
     */
    @GetMapping("/borrow-records")
    public Result<PageResult<BorrowRecord>> listMyBorrowRecords(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long borrowerId = jwtUtil.getUserIdFromToken(request.getHeader("token"));
        PageResult<BorrowRecord> result = borrowService.listMyBorrowRecords(borrowerId, page, pageSize);
        return Result.success(result);
    }

    /**
     * 接收推送偏好书籍
     */
    @GetMapping("/book-recommendations")
    public Result<List<BookPreference>> getBookRecommendations(HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromToken(request.getHeader("token"));
        List<BookPreference> result = borrowService.getBookRecommendations(userId);
        return Result.success(result);
    }

    @Data
    public static class BorrowRequest {
        private Long book_id;
    }
}

