package com.example.campus.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 图书借阅记录实体类
 */
@Data
public class BorrowRecord {
    private Long recordId;
    private Long bookId;
    private Long borrowerId;
    private Integer policyId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Integer status; // 1:借阅中, 2:已归还, 3:逾期未还
    private Integer renewalCount;
    private BigDecimal fineAmount;
}

