package com.example.campus.mapper;

import com.example.campus.entity.StudentGrade;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学生单项成绩Mapper接口
 */
@Mapper
public interface StudentGradeMapper {
    StudentGrade selectById(@Param("gradeId") Long gradeId);
    StudentGrade selectByStudentAndComponent(@Param("studentId") Long studentId, @Param("componentId") Long componentId);
    List<StudentGrade> selectByClassId(@Param("classId") Long classId);
    List<StudentGrade> selectByStudentIdAndClassId(@Param("studentId") Long studentId, @Param("classId") Long classId);
    int insert(StudentGrade grade);
    int update(StudentGrade grade);
    int deleteById(@Param("gradeId") Long gradeId);
}

