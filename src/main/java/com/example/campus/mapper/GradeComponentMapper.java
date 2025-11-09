package com.example.campus.mapper;

import com.example.campus.entity.GradeComponent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 成绩组成项Mapper接口
 */
@Mapper
public interface GradeComponentMapper {
    GradeComponent selectById(@Param("componentId") Long componentId);
    List<GradeComponent> selectByClassId(@Param("classId") Long classId);
    int insert(GradeComponent component);
    int update(GradeComponent component);
    int deleteById(@Param("componentId") Long componentId);
    int countByComponentId(@Param("componentId") Long componentId); // 检查是否有成绩录入
}

