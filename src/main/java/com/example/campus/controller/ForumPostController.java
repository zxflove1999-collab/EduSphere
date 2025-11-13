package com.example.campus.controller;

import com.example.campus.common.PageResult;
import com.example.campus.common.Result;
import com.example.campus.entity.ForumPost;
import com.example.campus.service.ForumPostService;
import com.example.campus.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 论坛帖子控制器
 */
@RestController
@RequestMapping("/forum/posts")
@RequiredArgsConstructor
public class ForumPostController {
    private final ForumPostService forumPostService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public Result<PageResult<Map<String, Object>>> listPosts(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer category_id,
            @RequestParam(required = false) Long author_id,
            @RequestParam(required = false) Integer post_status,
            @RequestParam(required = false) String title) {
        PageResult<Map<String, Object>> result = forumPostService.listPosts(page, pageSize, category_id, author_id, post_status, title);
        return Result.success(result);
    }

    @GetMapping("/top")
    public Result<List<ForumPost>> getTopPosts(@RequestParam(required = false) Integer category_id) {
        List<ForumPost> posts = forumPostService.getTopPosts(category_id);
        return Result.success(posts);
    }

    @GetMapping("/{id}")
    public Result<ForumPost> getPost(@PathVariable("id") Long id) {
        ForumPost post = forumPostService.getPost(id);
        return Result.success(post);
    }

    @PostMapping
    public Result<Void> createPost(@RequestBody ForumPost post,
                                   @RequestHeader("token") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        forumPostService.createPost(post, userId);
        return Result.success();
    }

    @PutMapping
    public Result<Void> updatePost(@RequestBody ForumPost post,
                                   @RequestHeader("token") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        forumPostService.updatePost(post, userId);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deletePost(@PathVariable("id") Long id,
                                   @RequestHeader("token") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        // 简化处理，实际应该判断是否为管理员
        boolean isAdmin = false;
        forumPostService.deletePost(id, userId, isAdmin);
        return Result.success();
    }

    @PutMapping("/{id}/top")
    public Result<Void> setTop(@PathVariable("id") Long id,
                               @RequestParam("is_top") Integer isTop) {
        forumPostService.setTop(id, isTop);
        return Result.success();
    }
}

