package com.example.campus.mapper;

import com.example.campus.entity.LeaveRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 请假申请Mapper接口
 */
@Mapper
public interface LeaveRequestMapper {
    LeaveRequest selectById(@Param("requestId") Long requestId);
    List<LeaveRequest> selectByStudentId(@Param("studentId") Long studentId);
    List<LeaveRequest> selectPendingByApproverId(@Param("approverId") Long approverId);
    int insert(LeaveRequest request);
    int update(LeaveRequest request);
}

