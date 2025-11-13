package com.example.campus.mapper;

import com.example.campus.entity.StudentProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 学生资料Mapper接口
 */
@Mapper
public interface StudentProfileMapper {
    StudentProfile selectByUserId(@Param("userId") Long userId);
    int insert(StudentProfile profile);
    int update(StudentProfile profile);
    int deleteByUserId(@Param("userId") Long userId);
}

