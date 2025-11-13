package com.example.campus.mapper;

import com.example.campus.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 书籍标签Mapper接口
 */
@Mapper
public interface TagMapper {
    Tag selectById(@Param("tagId") Integer tagId);
    Tag selectByTagName(@Param("tagName") String tagName);
    List<Tag> selectAll();
    int insert(Tag tag);
    int update(Tag tag);
    int deleteById(@Param("tagId") Integer tagId);
}

