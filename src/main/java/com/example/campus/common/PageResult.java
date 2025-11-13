package com.example.campus.common;

import lombok.Data;
import java.util.List;

/**
 * 分页结果类
 */
@Data
public class PageResult<T> {
    private Long total;
    private List<T> rows;

    public PageResult(Long total, List<T> rows) {
        this.total = total;
        this.rows = rows;
    }
}

