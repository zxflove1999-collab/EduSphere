package com.example.campus.mapper;

import com.example.campus.entity.DormPreference;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 宿舍偏好问卷 Mapper
 */
@Mapper
public interface DormPreferenceMapper {

    DormPreference selectByStudentId(@Param("studentId") Long studentId);

    int insert(DormPreference preference);

    int update(DormPreference preference);

    // ★ 新增：用于自动宿舍分配，获取全部学生偏好
    List<DormPreference> selectAll();
}


