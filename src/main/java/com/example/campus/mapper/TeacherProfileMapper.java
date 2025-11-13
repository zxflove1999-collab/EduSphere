package com.example.campus.mapper;

import com.example.campus.entity.TeacherProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 教师资料Mapper接口
 */
@Mapper
public interface TeacherProfileMapper {
    TeacherProfile selectByUserId(@Param("userId") Long userId);
    int insert(TeacherProfile profile);
    int update(TeacherProfile profile);
    int deleteByUserId(@Param("userId") Long userId);
}

