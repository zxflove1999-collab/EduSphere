package com.example.campus.controller;

import com.example.campus.common.Result;
import com.example.campus.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 借阅记录控制器 (学生/管理员)
 */
@RestController
@RequestMapping("/borrow-records")
@RequiredArgsConstructor
public class BorrowRecordController {
    private final BorrowService borrowService;

    /**
     * 归还书籍
     */
    @PutMapping("/{record_id}/return")
    public Result<Void> returnBook(@PathVariable("record_id") Long recordId) {
        borrowService.returnBook(recordId);
        return Result.success();
    }
}

