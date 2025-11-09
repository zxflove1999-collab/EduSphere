package com.example.campus.mapper;

import com.example.campus.entity.BookTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 书籍与标签关联Mapper接口
 */
@Mapper
public interface BookTagMapper {
    BookTag selectById(@Param("id") Long id);
    List<BookTag> selectByBookId(@Param("bookId") Long bookId);
    List<BookTag> selectByTagId(@Param("tagId") Integer tagId);
    int insert(BookTag bookTag);
    int deleteById(@Param("id") Long id);
    int deleteByBookId(@Param("bookId") Long bookId);
    int deleteByBookIdAndTagId(@Param("bookId") Long bookId, @Param("tagId") Integer tagId);
}

