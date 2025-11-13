package com.example.campus.controller;

import com.example.campus.common.Result;
import com.example.campus.entity.BookPolicy;
import com.example.campus.service.BookPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 借阅策略管理控制器
 */
@RestController
@RequestMapping("/admin/book-policies")
@RequiredArgsConstructor
public class BookPolicyController {
    private final BookPolicyService bookPolicyService;

    /**
     * 设置借书时长 (新增/修改)
     */
    @PostMapping
    public Result<Void> addBookPolicy(@RequestBody BookPolicy policy) {
        bookPolicyService.saveBookPolicy(policy);
        return Result.success();
    }

    /**
     * 修改借阅策略
     */
    @PutMapping
    public Result<Void> updateBookPolicy(@RequestBody BookPolicy policy) {
        bookPolicyService.saveBookPolicy(policy);
        return Result.success();
    }

    /**
     * 查询所有借阅策略
     */
    @GetMapping
    public Result<List<BookPolicy>> listAllPolicies() {
        List<BookPolicy> result = bookPolicyService.listAllPolicies();
        return Result.success(result);
    }
}

