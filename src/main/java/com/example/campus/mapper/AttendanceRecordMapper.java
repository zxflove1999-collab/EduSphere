package com.example.campus.mapper;

import com.example.campus.entity.AttendanceRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学生考勤记录Mapper接口
 */
@Mapper
public interface AttendanceRecordMapper {
    AttendanceRecord selectById(@Param("recordId") Long recordId);
    AttendanceRecord selectBySessionAndStudent(@Param("sessionId") Long sessionId, @Param("studentId") Long studentId);
    List<AttendanceRecord> selectBySessionId(@Param("sessionId") Long sessionId, @Param("status") Integer status);
    List<AttendanceRecord> selectByStudentIdAndClassId(@Param("studentId") Long studentId, @Param("classId") Long classId);
    int insert(AttendanceRecord record);
    int update(AttendanceRecord record);
}

