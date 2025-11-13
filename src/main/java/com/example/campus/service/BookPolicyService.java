package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.entity.BookPolicy;
import com.example.campus.mapper.BookPolicyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
        if (policy == null) {
            throw new BusinessException("请求体不能为空");
        }
        if (!StringUtils.hasText(policy.getPolicyName())) {
            throw new BusinessException("policy_name为必填项");
        }
        if (policy.getUserTypeScope() == null) {
            throw new BusinessException("user_type_scope为必填项");
        }
        if (policy.getMaxDurationDays() == null || policy.getMaxDurationDays() <= 0) {
            throw new BusinessException("max_duration_days必须为正整数");
        }
        if (policy.getMaxBorrowCount() == null || policy.getMaxBorrowCount() <= 0) {
            throw new BusinessException("max_borrow_count必须为正整数");
        }
        if (policy.getAllowRenewalCount() != null && policy.getAllowRenewalCount() < 0) {
            throw new BusinessException("allow_renewal_count不能为负数");
        }
        policy.setPolicyName(policy.getPolicyName().trim());
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

