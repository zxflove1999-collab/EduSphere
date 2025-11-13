package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.common.PageResult;
import com.example.campus.entity.LeaveRequest;
import com.example.campus.mapper.LeaveRequestMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 请假管理服务
 */
@Service
@RequiredArgsConstructor
public class LeaveRequestService {
    private final LeaveRequestMapper leaveRequestMapper;

    /**
     * 提交请假申请 (学生)
     */
    @Transactional
    public void submitLeaveRequest(Long studentId, Long classId, String reason,
                                   java.time.LocalDateTime startTime, java.time.LocalDateTime endTime,
                                   Long approverId) {
        LeaveRequest request = new LeaveRequest();
        request.setStudentId(studentId);
        request.setClassId(classId);
        request.setReason(reason);
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setApproverId(approverId);
        request.setStatus(1); // 待审批
        leaveRequestMapper.insert(request);
    }

    /**
     * 查询我的请假申请 (学生)
     */
    public PageResult<LeaveRequest> listMyLeaveRequests(Long studentId, Integer page, Integer pageSize) {
        PageHelper.startPage(page != null ? page : 1, pageSize != null ? pageSize : 10);
        List<LeaveRequest> requests = leaveRequestMapper.selectByStudentId(studentId);
        PageInfo<LeaveRequest> pageInfo = new PageInfo<>(requests);
        return new PageResult<>(pageInfo.getTotal(), requests);
    }

    /**
     * 查询待我审批的请假 (教师)
     */
    public PageResult<LeaveRequest> listPendingLeaveRequests(Long approverId, Integer page, Integer pageSize) {
        PageHelper.startPage(page != null ? page : 1, pageSize != null ? pageSize : 10);
        List<LeaveRequest> requests = leaveRequestMapper.selectPendingByApproverId(approverId);
        PageInfo<LeaveRequest> pageInfo = new PageInfo<>(requests);
        return new PageResult<>(pageInfo.getTotal(), requests);
    }

    /**
     * 审批请假申请 (教师)
     */
    @Transactional
    public void approveLeaveRequest(Long requestId, Integer status, String approvalRemarks, Long approverId) {
        LeaveRequest request = leaveRequestMapper.selectById(requestId);
        if (request == null) {
            throw new BusinessException("请假申请不存在");
        }
        if (request.getStatus() != 1) {
            throw new BusinessException("该请假申请已处理");
        }
        if (!request.getApproverId().equals(approverId)) {
            throw new BusinessException("您无权审批该请假申请");
        }
        
        request.setStatus(status);
        request.setApprovalRemarks(approvalRemarks);
        leaveRequestMapper.update(request);
    }
}

