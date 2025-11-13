package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.entity.ForumComment;
import com.example.campus.mapper.ForumCommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 论坛评论服务
 */
@Service
@RequiredArgsConstructor
public class ForumCommentService {
    private final ForumCommentMapper forumCommentMapper;

    public List<Map<String, Object>> getCommentsByPostId(Long postId) {
        return forumCommentMapper.selectByPostId(postId);
    }

    public List<ForumComment> getRepliesByCommentId(Long parentCommentId) {
        return forumCommentMapper.selectByParentCommentId(parentCommentId);
    }

    public List<ForumComment> getCommentsByAuthorId(Long authorId) {
        return forumCommentMapper.selectByAuthorId(authorId);
    }

    @Transactional
    public void createComment(ForumComment comment, Long authorId) {
        comment.setAuthorId(authorId);
        if (comment.getIsDeleted() == null) {
            comment.setIsDeleted(0); // 默认未删除
        }
        forumCommentMapper.insert(comment);
    }

    @Transactional
    public void updateComment(ForumComment comment, Long authorId) {
        ForumComment existing = forumCommentMapper.selectById(comment.getCommentId());
        if (existing == null) {
            throw new BusinessException("评论不存在");
        }
        if (!existing.getAuthorId().equals(authorId)) {
            throw new BusinessException("无权修改此评论");
        }
        if (existing.getIsDeleted() == 1) {
            throw new BusinessException("评论已删除，无法修改");
        }
        comment.setAuthorId(authorId);
        forumCommentMapper.update(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long operatorId, boolean isAdmin) {
        ForumComment comment = forumCommentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        if (!isAdmin && !comment.getAuthorId().equals(operatorId)) {
            throw new BusinessException("无权删除此评论");
        }
        forumCommentMapper.updateStatus(commentId, 1);
    }
}

