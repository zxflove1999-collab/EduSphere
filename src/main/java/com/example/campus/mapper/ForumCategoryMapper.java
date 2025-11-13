package com.example.campus.mapper;

import com.example.campus.entity.ForumCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 论坛板块Mapper接口
 */
@Mapper
public interface ForumCategoryMapper {
    ForumCategory selectById(@Param("categoryId") Integer categoryId);
    List<ForumCategory> selectAll(@Param("isActive") Integer isActive);
    int insert(ForumCategory category);
    int update(ForumCategory category);
    int deleteById(@Param("categoryId") Integer categoryId);
}

