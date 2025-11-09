package com.example.campus.mapper;

import com.example.campus.entity.Class;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 开课班级Mapper接口
 */
@Mapper
public interface ClassMapper {
    Class selectById(@Param("classId") Long classId);
    List<Class> selectByTeacherId(@Param("teacherId") Long teacherId,
                                  @Param("courseName") String courseName,
                                  @Param("semester") String semester,
                                  @Param("status") Integer status);
    List<Class> selectAvailableClasses(@Param("courseName") String courseName,
                                       @Param("teacherName") String teacherName,
                                       @Param("departmentId") Integer departmentId);
    int insert(Class clazz);
    int update(Class clazz);
    int deleteById(@Param("classId") Long classId);
    int countByClassId(@Param("classId") Long classId); // 检查是否有学生选课
    int updateCurrentEnrollment(@Param("classId") Long classId, @Param("delta") Integer delta);
}

