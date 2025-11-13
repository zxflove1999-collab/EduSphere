package com.example.campus.mapper;

import com.example.campus.entity.Homework;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 作业发布Mapper接口
 */
@Mapper
public interface HomeworkMapper {
    Homework selectById(@Param("homeworkId") Long homeworkId);
    List<Homework> selectByClassId(@Param("classId") Long classId);
    int insert(Homework homework);
    int update(Homework homework);
    int deleteById(@Param("homeworkId") Long homeworkId);
    int countSubmissions(@Param("homeworkId") Long homeworkId);
}

