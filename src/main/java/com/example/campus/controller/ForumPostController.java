package com.example.campus.controller;

import com.example.campus.common.PageResult;
import com.example.campus.common.Result;
import com.example.campus.entity.ForumPost;
import com.example.campus.service.ForumPostService;
import com.example.campus.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam(required = false, name = "category_id") Integer categoryId,
            @RequestParam(required = false, name = "search_keyword") String searchKeyword,
            @RequestParam(required = false, name = "is_top") Integer isTop) {
        PageResult<Map<String, Object>> result = forumPostService.listPosts(page, pageSize, categoryId, searchKeyword, isTop);
        return Result.success(result);
    }

    @GetMapping("/{post_id}")
    public Result<Map<String, Object>> getPost(@PathVariable("post_id") Long postId) {
        Map<String, Object> detail = forumPostService.getPostDetail(postId);
        return Result.success(detail);
    }

    @PostMapping
    public Result<Void> createPost(@RequestBody ForumPost post,
                                   @RequestHeader("token") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        forumPostService.createPost(post, userId);
        return Result.success();
    }

    @DeleteMapping("/{post_id}")
    public Result<Void> deletePost(@PathVariable("post_id") Long postId,
                                   @RequestHeader("token") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        boolean isAdmin = false;
        forumPostService.deletePost(postId, userId, isAdmin);
        return Result.success();
    }
}