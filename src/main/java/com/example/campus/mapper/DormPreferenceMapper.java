package com.example.campus.mapper;

import com.example.campus.entity.DormPreference;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 宿舍偏好问卷Mapper
 */
@Mapper
public interface DormPreferenceMapper {
    DormPreference selectByStudentId(@Param("studentId") Long studentId);
    int insert(DormPreference preference);
    int update(DormPreference preference);
}

