package com.example.campus.mapper;

import com.example.campus.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 书籍信息Mapper接口
 */
@Mapper
public interface BookMapper {
    Book selectById(@Param("bookId") Long bookId);
    Book selectByIsbn(@Param("isbn") String isbn);
    List<Book> selectByCondition(@Param("title") String title,
                                 @Param("author") String author,
                                 @Param("tagName") String tagName,
                                 @Param("isAdminView") Boolean isAdminView);
    int insert(Book book);
    int update(Book book);
    int updateAvailableCopies(@Param("bookId") Long bookId, @Param("delta") Integer delta);
    int deleteById(@Param("bookId") Long bookId);
}

