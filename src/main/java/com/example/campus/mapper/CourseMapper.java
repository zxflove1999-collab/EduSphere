package com.example.campus.mapper;

import com.example.campus.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 课程模板Mapper接口
 */
@Mapper
public interface CourseMapper {
    Course selectById(@Param("courseId") Long courseId);
    Course selectByCourseCode(@Param("courseCode") String courseCode);
    List<Course> selectByCondition(@Param("courseName") String courseName,
                                    @Param("courseCode") String courseCode,
                                    @Param("departmentId") Integer departmentId);
    List<Map<String, Object>> selectSimpleList();
    int insert(Course course);
    int update(Course course);
    int deleteById(@Param("courseId") Long courseId);
    int countByCourseId(@Param("courseId") Long courseId); // 检查是否有班级关联
}

