package com.example.campus.mapper;

import com.example.campus.entity.AttendanceSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 考勤任务Mapper接口
 */
@Mapper
public interface AttendanceSessionMapper {
    AttendanceSession selectById(@Param("sessionId") Long sessionId);
    AttendanceSession selectActiveByClassId(@Param("classId") Long classId);
    List<AttendanceSession> selectByClassId(@Param("classId") Long classId);
    int insert(AttendanceSession session);
    int update(AttendanceSession session);
    int updateStatus(@Param("sessionId") Long sessionId, @Param("status") Integer status);
}

