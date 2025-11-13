package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.entity.BookPolicy;
import com.example.campus.mapper.BookPolicyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 借阅策略管理服务
 */
@Service
@RequiredArgsConstructor
public class BookPolicyService {
    private final BookPolicyMapper bookPolicyMapper;

    /**
     * 设置借书时长 (新增/修改)
     */
    @Transactional
    public void saveBookPolicy(BookPolicy policy) {
        if (policy.getPolicyId() != null) {
            // 修改
            BookPolicy existing = bookPolicyMapper.selectById(policy.getPolicyId());
            if (existing == null) {
                throw new BusinessException("借阅策略不存在");
            }
            bookPolicyMapper.update(policy);
        } else {
            // 新增
            bookPolicyMapper.insert(policy);
        }
    }

    /**
     * 查询所有借阅策略
     */
    public List<BookPolicy> listAllPolicies() {
        return bookPolicyMapper.selectAll();
    }

    /**
     * 根据用户类型获取借阅策略
     */
    public BookPolicy getPolicyByUserType(Integer userTypeScope) {
        return bookPolicyMapper.selectByUserType(userTypeScope);
    }
}

