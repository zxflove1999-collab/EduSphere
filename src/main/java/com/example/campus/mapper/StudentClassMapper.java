package com.example.campus.mapper;

import com.example.campus.entity.StudentClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学生选课Mapper接口
 */
@Mapper
public interface StudentClassMapper {
    StudentClass selectById(@Param("id") Long id);
    StudentClass selectByStudentAndClass(@Param("studentId") Long studentId, @Param("classId") Long classId);
    List<StudentClass> selectByStudentId(@Param("studentId") Long studentId, @Param("semester") String semester);
    List<StudentClass> selectByClassId(@Param("classId") Long classId);
    List<java.util.Map<String, Object>> selectStudentsByClassId(@Param("classId") Long classId);
    int insert(StudentClass studentClass);
    int update(StudentClass studentClass);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    int updateFinalScore(@Param("id") Long id, @Param("finalScore") java.math.BigDecimal finalScore);
}

