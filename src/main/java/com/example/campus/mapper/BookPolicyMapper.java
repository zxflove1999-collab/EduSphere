package com.example.campus.mapper;

import com.example.campus.entity.BookPolicy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 借阅策略Mapper接口
 */
@Mapper
public interface BookPolicyMapper {
    BookPolicy selectById(@Param("policyId") Integer policyId);
    BookPolicy selectByUserType(@Param("userTypeScope") Integer userTypeScope);
    List<BookPolicy> selectAll();
    int insert(BookPolicy policy);
    int update(BookPolicy policy);
    int deleteById(@Param("policyId") Integer policyId);
}

