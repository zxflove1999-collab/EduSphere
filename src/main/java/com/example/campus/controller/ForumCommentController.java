package com.example.campus.controller;

import com.example.campus.common.Result;
import com.example.campus.entity.ForumComment;
import com.example.campus.service.ForumCommentService;
import com.example.campus.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 论坛评论控制器
 */
@RestController
@RequestMapping("/forum/comments")
@RequiredArgsConstructor
public class ForumCommentController {
    private final ForumCommentService forumCommentService;
    private final JwtUtil jwtUtil;

    @GetMapping("/post/{post_id}")
    public Result<List<Map<String, Object>>> getCommentsByPost(@PathVariable("post_id") Long postId) {
        List<Map<String, Object>> comments = forumCommentService.getCommentsByPostId(postId);
        return Result.success(comments);
    }

    @GetMapping("/reply/{comment_id}")
    public Result<List<ForumComment>> getReplies(@PathVariable("comment_id") Long commentId) {
        List<ForumComment> replies = forumCommentService.getRepliesByCommentId(commentId);
        return Result.success(replies);
    }

    @PostMapping
    public Result<Void> createComment(@RequestBody ForumComment comment,
                                     @RequestHeader("token") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        forumCommentService.createComment(comment, userId);
        return Result.success();
    }

    @PutMapping
    public Result<Void> updateComment(@RequestBody ForumComment comment,
                                     @RequestHeader("token") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        forumCommentService.updateComment(comment, userId);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable("id") Long id,
                                     @RequestHeader("token") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        // 简化处理，实际应该判断是否为管理员
        boolean isAdmin = false;
        forumCommentService.deleteComment(id, userId, isAdmin);
        return Result.success();
    }
}

