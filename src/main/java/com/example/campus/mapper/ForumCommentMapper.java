package com.example.campus.mapper;

import com.example.campus.entity.ForumComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 论坛评论Mapper接口
 */
@Mapper
public interface ForumCommentMapper {
    ForumComment selectById(@Param("commentId") Long commentId);
    List<java.util.Map<String, Object>> selectByPostId(@Param("postId") Long postId);
    List<ForumComment> selectByParentCommentId(@Param("parentCommentId") Long parentCommentId);
    List<ForumComment> selectByAuthorId(@Param("authorId") Long authorId);
    int insert(ForumComment comment);
    int update(ForumComment comment);
    int updateStatus(@Param("commentId") Long commentId, @Param("isDeleted") Integer isDeleted);
    int deleteById(@Param("commentId") Long commentId);
}

