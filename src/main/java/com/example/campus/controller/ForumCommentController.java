package com.example.campus.controller;

import com.example.campus.common.Result;
import com.example.campus.entity.ForumComment;
import com.example.campus.service.ForumCommentService;
import com.example.campus.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 论坛评论控制器
 */
@RestController
@RequestMapping("/forum")
@RequiredArgsConstructor
public class ForumCommentController {
    private final ForumCommentService forumCommentService;
    private final JwtUtil jwtUtil;

    @PostMapping("/posts/{post_id}/comments")
    public Result<Void> createComment(@PathVariable("post_id") Long postId,
                                      @RequestBody ForumComment comment,
                                      @RequestHeader("token") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        comment.setPostId(postId);
        forumCommentService.createComment(comment, userId);
        return Result.success();
    }

    @DeleteMapping("/comments/{comment_id}")
    public Result<Void> deleteComment(@PathVariable("comment_id") Long commentId,
                                      @RequestHeader("token") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        boolean isAdmin = false;
        forumCommentService.deleteComment(commentId, userId, isAdmin);
        return Result.success();
    }
}