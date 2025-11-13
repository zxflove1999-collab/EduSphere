package com.example.campus.mapper;

import com.example.campus.entity.ForumPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 论坛帖子Mapper接口
 */
@Mapper
public interface ForumPostMapper {
    ForumPost selectById(@Param("postId") Long postId);
    java.util.Map<String, Object> selectDetailById(@Param("postId") Long postId);
    List<java.util.Map<String, Object>> selectByCondition(@Param("categoryId") Integer categoryId,
                                      @Param("searchKeyword") String searchKeyword,
                                      @Param("isTop") Integer isTop);
    List<ForumPost> selectTopPosts(@Param("categoryId") Integer categoryId);
    int insert(ForumPost post);
    int update(ForumPost post);
    int updateViewCount(@Param("postId") Long postId);
    int updateStatus(@Param("postId") Long postId, @Param("postStatus") Integer postStatus,
                     @Param("deletedBy") Long deletedBy);
    int deleteById(@Param("postId") Long postId);
}

