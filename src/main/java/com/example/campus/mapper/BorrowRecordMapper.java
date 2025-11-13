package com.example.campus.mapper;

import com.example.campus.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 图书借阅记录Mapper接口
 */
@Mapper
public interface BorrowRecordMapper {
    BorrowRecord selectById(@Param("recordId") Long recordId);
    List<BorrowRecord> selectByBorrowerId(@Param("borrowerId") Long borrowerId);
    List<BorrowRecord> selectByBookId(@Param("bookId") Long bookId);
    List<BorrowRecord> selectBorrowingByBorrowerId(@Param("borrowerId") Long borrowerId);
    int insert(BorrowRecord record);
    int update(BorrowRecord record);
    int updateReturnDate(@Param("recordId") Long recordId, @Param("returnDate") java.time.LocalDate returnDate);
    int deleteById(@Param("recordId") Long recordId);
}

