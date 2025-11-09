package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.common.PageResult;
import com.example.campus.entity.ForumPost;
import com.example.campus.mapper.ForumPostMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 论坛帖子服务
 */
@Service
@RequiredArgsConstructor
public class ForumPostService {
    private final ForumPostMapper forumPostMapper;

    public PageResult<Map<String, Object>> listPosts(Integer page, Integer pageSize, Integer categoryId,
                                                     Long authorId, Integer postStatus, String title) {
        PageHelper.startPage(page != null ? page : 1, pageSize != null ? pageSize : 10);
        List<Map<String, Object>> posts = forumPostMapper.selectByCondition(categoryId, authorId, postStatus, title);
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(posts);
        return new PageResult<>(pageInfo.getTotal(), posts);
    }

    public ForumPost getPost(Long postId) {
        ForumPost post = forumPostMapper.selectById(postId);
        if (post != null && post.getPostStatus() == 1) {
            forumPostMapper.updateViewCount(postId);
        }
        return post;
    }

    public List<ForumPost> getTopPosts(Integer categoryId) {
        return forumPostMapper.selectTopPosts(categoryId);
    }

    @Transactional
    public void createPost(ForumPost post, Long authorId) {
        post.setAuthorId(authorId);
        if (post.getPostStatus() == null) {
            post.setPostStatus(1); // 默认正常
        }
        if (post.getIsTop() == null) {
            post.setIsTop(0); // 默认不置顶
        }
        if (post.getViewCount() == null) {
            post.setViewCount(0);
        }
        forumPostMapper.insert(post);
    }

    @Transactional
    public void updatePost(ForumPost post, Long authorId) {
        ForumPost existing = forumPostMapper.selectById(post.getPostId());
        if (existing == null) {
            throw new BusinessException("帖子不存在");
        }
        if (!existing.getAuthorId().equals(authorId)) {
            throw new BusinessException("无权修改此帖子");
        }
        if (existing.getPostStatus() != 1) {
            throw new BusinessException("帖子已删除，无法修改");
        }
        post.setAuthorId(authorId);
        forumPostMapper.update(post);
    }

    @Transactional
    public void deletePost(Long postId, Long operatorId, boolean isAdmin) {
        ForumPost post = forumPostMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }
        if (!isAdmin && !post.getAuthorId().equals(operatorId)) {
            throw new BusinessException("无权删除此帖子");
        }
        forumPostMapper.updateStatus(postId, 2, operatorId);
    }

    @Transactional
    public void setTop(Long postId, Integer isTop) {
        ForumPost post = forumPostMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }
        post.setIsTop(isTop);
        forumPostMapper.update(post);
    }
}

