package com.example.campus.mapper;

import com.example.campus.entity.ClassSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课表时间地点Mapper接口
 */
@Mapper
public interface ClassScheduleMapper {
    List<ClassSchedule> selectByClassId(@Param("classId") Long classId);
    List<ClassSchedule> selectByStudentId(@Param("studentId") Long studentId, @Param("semester") String semester);
    int insert(ClassSchedule schedule);
    int deleteByClassId(@Param("classId") Long classId);
    int deleteById(@Param("scheduleId") Long scheduleId);
}

