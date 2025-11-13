package com.example.campus.controller;

import com.example.campus.common.Result;
import com.example.campus.service.ForumPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 论坛帖子管理员控制器
 */
@RestController
@RequestMapping("/admin/forum/posts")
@RequiredArgsConstructor
public class AdminForumPostController {

    private final ForumPostService forumPostService;

    @PutMapping("/{post_id}/top")
    public Result<Void> setTop(@PathVariable("post_id") Long postId,
                               @RequestParam("is_top") Integer isTop) {
        forumPostService.setTop(postId, isTop);
        return Result.success();
    }
}

