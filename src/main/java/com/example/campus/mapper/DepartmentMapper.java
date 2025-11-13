package com.example.campus.mapper;

import com.example.campus.entity.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 院系Mapper接口
 */
@Mapper
public interface DepartmentMapper {
    Department selectById(@Param("departmentId") Integer departmentId);
    List<Department> selectByCondition(@Param("departmentName") String departmentName);
    List<Map<String, Object>> selectSimpleList();
    int insert(Department department);
    int update(Department department);
    int deleteById(@Param("departmentId") Integer departmentId);
}

