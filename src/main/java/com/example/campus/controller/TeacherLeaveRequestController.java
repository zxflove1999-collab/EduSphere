package com.example.campus.controller;

import com.example.campus.common.PageResult;
import com.example.campus.common.Result;
import com.example.campus.entity.LeaveRequest;
import com.example.campus.service.LeaveRequestService;
import com.example.campus.util.JwtUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 请假管理控制器 (教师)
 */
@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherLeaveRequestController {
    private final LeaveRequestService leaveRequestService;
    private final JwtUtil jwtUtil;

    /**
     * 查询待我审批的请假
     */
    @GetMapping("/leave-requests/pending")
    public Result<PageResult<LeaveRequest>> listPendingLeaveRequests(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long approverId = jwtUtil.getUserIdFromToken(request.getHeader("token"));
        PageResult<LeaveRequest> result = leaveRequestService.listPendingLeaveRequests(approverId, page, pageSize);
        return Result.success(result);
    }

    /**
     * 审批请假申请
     */
    @PutMapping("/leave-requests/{request_id}/approve")
    public Result<Void> approveLeaveRequest(
            @PathVariable("request_id") Long requestId,
            @RequestBody LeaveRequestApproveRequest request,
            HttpServletRequest httpRequest) {
        Long approverId = jwtUtil.getUserIdFromToken(httpRequest.getHeader("token"));
        leaveRequestService.approveLeaveRequest(requestId, request.getStatus(), request.getApproval_remarks(), approverId);
        return Result.success();
    }

    @Data
    public static class LeaveRequestApproveRequest {
        private Integer status;
        private String approval_remarks;
    }
}

