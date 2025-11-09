package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.common.PageResult;
import com.example.campus.entity.Book;
import com.example.campus.entity.BookPreference;
import com.example.campus.entity.BookPolicy;
import com.example.campus.entity.BorrowRecord;
import com.example.campus.mapper.BookMapper;
import com.example.campus.mapper.BookPolicyMapper;
import com.example.campus.mapper.BookPreferenceMapper;
import com.example.campus.mapper.BorrowRecordMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 图书借阅服务
 */
@Service
@RequiredArgsConstructor
public class BorrowService {
    private final BorrowRecordMapper borrowRecordMapper;
    private final BookMapper bookMapper;
    private final BookPolicyMapper bookPolicyMapper;
    private final BookPreferenceMapper bookPreferenceMapper;

    /**
     * 借阅书籍
     */
    @Transactional
    public void borrowBook(Long bookId, Long borrowerId, Integer userTypeScope) {
        // 检查书籍是否存在且有库存
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new BusinessException("书籍不存在");
        }
        if (book.getAvailableCopies() <= 0) {
            throw new BusinessException("书籍已借完");
        }
        
        // 获取借阅策略
        BookPolicy policy = bookPolicyMapper.selectByUserType(userTypeScope);
        if (policy == null) {
            throw new BusinessException("未找到适用的借阅策略");
        }
        
        // 检查借阅数量限制
        List<BorrowRecord> borrowing = borrowRecordMapper.selectBorrowingByBorrowerId(borrowerId);
        if (borrowing.size() >= policy.getMaxBorrowCount()) {
            throw new BusinessException("已达到最大借阅数量限制");
        }
        
        // 创建借阅记录
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(policy.getMaxDurationDays());
        
        BorrowRecord record = new BorrowRecord();
        record.setBookId(bookId);
        record.setBorrowerId(borrowerId);
        record.setPolicyId(policy.getPolicyId());
        record.setBorrowDate(borrowDate);
        record.setDueDate(dueDate);
        record.setStatus(1); // 借阅中
        record.setRenewalCount(0);
        record.setFineAmount(java.math.BigDecimal.ZERO);
        borrowRecordMapper.insert(record);
        
        // 减少库存
        bookMapper.updateAvailableCopies(bookId, -1);
    }

    /**
     * 归还书籍
     */
    @Transactional
    public void returnBook(Long recordId) {
        BorrowRecord record = borrowRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("借阅记录不存在");
        }
        if (record.getStatus() != 1) {
            throw new BusinessException("该书籍已归还");
        }
        
        LocalDate returnDate = LocalDate.now();
        record.setReturnDate(returnDate);
        record.setStatus(2); // 已归还
        borrowRecordMapper.updateReturnDate(recordId, returnDate);
        
        // 增加库存
        bookMapper.updateAvailableCopies(record.getBookId(), 1);
    }

    /**
     * 我的借阅记录
     */
    public PageResult<BorrowRecord> listMyBorrowRecords(Long borrowerId, Integer page, Integer pageSize) {
        PageHelper.startPage(page != null ? page : 1, pageSize != null ? pageSize : 10);
        List<BorrowRecord> records = borrowRecordMapper.selectByBorrowerId(borrowerId);
        PageInfo<BorrowRecord> pageInfo = new PageInfo<>(records);
        return new PageResult<>(pageInfo.getTotal(), records);
    }

    /**
     * 接收推送偏好书籍
     */
    public List<BookPreference> getBookRecommendations(Long userId) {
        return bookPreferenceMapper.selectByUserId(userId);
    }
}

