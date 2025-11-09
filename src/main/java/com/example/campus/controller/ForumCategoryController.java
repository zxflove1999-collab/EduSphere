package com.example.campus.controller;

import com.example.campus.common.Result;
import com.example.campus.entity.ForumCategory;
import com.example.campus.service.ForumCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 论坛板块管理控制器
 */
@RestController
@RequestMapping("/admin/forum-categories")
@RequiredArgsConstructor
public class ForumCategoryController {
    private final ForumCategoryService forumCategoryService;

    @GetMapping
    public Result<List<ForumCategory>> listCategories(@RequestParam(required = false) Integer is_active) {
        List<ForumCategory> categories = forumCategoryService.listCategories(is_active);
        return Result.success(categories);
    }

    @GetMapping("/{id}")
    public Result<ForumCategory> getCategory(@PathVariable("id") Integer id) {
        ForumCategory category = forumCategoryService.getCategory(id);
        return Result.success(category);
    }

    @PostMapping
    public Result<Void> addCategory(@RequestBody ForumCategory category) {
        forumCategoryService.saveCategory(category);
        return Result.success();
    }

    @PutMapping
    public Result<Void> updateCategory(@RequestBody ForumCategory category) {
        forumCategoryService.saveCategory(category);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable("id") Integer id) {
        forumCategoryService.deleteCategory(id);
        return Result.success();
    }
}

