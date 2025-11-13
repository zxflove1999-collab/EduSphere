package com.example.campus.controller;

import com.example.campus.common.Result;
import com.example.campus.service.ForumCategoryService;
import com.example.campus.vo.ForumCategorySummaryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 论坛板块控制器
 */
@RestController
@RequestMapping("/forum/categories")
@RequiredArgsConstructor
public class ForumCategoryController {
    private final ForumCategoryService forumCategoryService;

    @GetMapping
    public Result<List<ForumCategorySummaryVO>> listCategories() {
        List<ForumCategorySummaryVO> categories = forumCategoryService.listCategorySummaries();
        return Result.success(categories);
    }
}