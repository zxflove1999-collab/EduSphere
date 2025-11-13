package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.entity.ForumCategory;
import com.example.campus.mapper.ForumCategoryMapper;
import com.example.campus.vo.ForumCategorySummaryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 论坛板块服务
 */
@Service
@RequiredArgsConstructor
public class ForumCategoryService {
    private final ForumCategoryMapper forumCategoryMapper;

    public List<ForumCategory> listCategories(Integer isActive) {
        return forumCategoryMapper.selectAll(isActive);
    }

    public List<ForumCategorySummaryVO> listCategorySummaries() {
        List<Map<String, Object>> rows = forumCategoryMapper.selectWithPostCount();
        return rows.stream().map(row -> {
            ForumCategorySummaryVO vo = new ForumCategorySummaryVO();
            vo.setCategoryId(((Number) row.get("category_id")).intValue());
            vo.setCategoryName((String) row.get("category_name"));
            Object countObj = row.get("post_count");
            vo.setPostCount(countObj != null ? ((Number) countObj).longValue() : 0L);
            return vo;
        }).collect(Collectors.toList());
    }

    public ForumCategory getCategory(Integer categoryId) {
        return forumCategoryMapper.selectById(categoryId);
    }

    @Transactional
    public void saveCategory(ForumCategory category) {
        if (category.getCategoryId() != null) {
            ForumCategory existing = forumCategoryMapper.selectById(category.getCategoryId());
            if (existing == null) {
                throw new BusinessException("板块不存在");
            }
            forumCategoryMapper.update(category);
        } else {
            if (category.getIsActive() == null) {
                category.setIsActive(1); // 默认启用
            }
            forumCategoryMapper.insert(category);
        }
    }

    @Transactional
    public void deleteCategory(Integer categoryId) {
        ForumCategory existing = forumCategoryMapper.selectById(categoryId);
        if (existing == null) {
            throw new BusinessException("板块不存在");
        }
        forumCategoryMapper.deleteById(categoryId);
    }
}

