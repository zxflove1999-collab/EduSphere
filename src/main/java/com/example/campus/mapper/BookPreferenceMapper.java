package com.example.campus.mapper;

import com.example.campus.entity.BookPreference;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 书籍偏好/推荐Mapper接口
 */
@Mapper
public interface BookPreferenceMapper {
    BookPreference selectById(@Param("id") Long id);
    List<BookPreference> selectByUserId(@Param("userId") Long userId);
    int insert(BookPreference preference);
    int update(BookPreference preference);
    int deleteById(@Param("id") Long id);
    int deleteByUserId(@Param("userId") Long userId);
}

