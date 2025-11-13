package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.common.PageResult;
import com.example.campus.entity.ForumPost;
import com.example.campus.mapper.ForumCommentMapper;
import com.example.campus.mapper.ForumPostMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 论坛帖子服务
 */
@Service
@RequiredArgsConstructor
public class ForumPostService {
    private final ForumPostMapper forumPostMapper;
    private final ForumCommentMapper forumCommentMapper;

    public PageResult<Map<String, Object>> listPosts(Integer page, Integer pageSize, Integer categoryId,
                                                     String searchKeyword, Integer isTop) {
        PageHelper.startPage(page != null ? page : 1, pageSize != null ? pageSize : 10);
        List<Map<String, Object>> posts = forumPostMapper.selectByCondition(categoryId, searchKeyword, isTop);
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

    public Map<String, Object> getPostDetail(Long postId) {
        Map<String, Object> postDetail = forumPostMapper.selectDetailById(postId);
        if (postDetail == null) {
            throw new BusinessException("帖子不存在");
        }
        Integer status = postDetail.get("post_status") != null ? ((Number) postDetail.get("post_status")).intValue() : 0;
        if (status != 1) {
            throw new BusinessException("帖子已下线");
        }
        forumPostMapper.updateViewCount(postId);

        Map<String, Object> postMap = new HashMap<>();
        postMap.put("post_id", postDetail.get("post_id"));
        postMap.put("title", postDetail.get("title"));
        postMap.put("content", postDetail.get("content"));
        postMap.put("author_name", postDetail.get("author_name"));
        postMap.put("created_at", postDetail.get("created_at"));
        postMap.put("view_count", postDetail.get("view_count"));

        List<Map<String, Object>> comments = forumCommentMapper.selectByPostId(postId);

        Map<String, Object> result = new HashMap<>();
        result.put("post", postMap);
        result.put("comments", comments);
        return result;
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

