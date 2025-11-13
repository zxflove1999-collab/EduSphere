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
import java.time.LocalDateTime;

/**
 * 请假管理控制器
 */
@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class LeaveRequestController {
    private final LeaveRequestService leaveRequestService;
    private final JwtUtil jwtUtil;

    /**
     * 提交请假申请
     */
    @PostMapping("/leave-requests")
    public Result<Void> submitLeaveRequest(
            @RequestBody LeaveRequestCreateRequest request,
            HttpServletRequest httpRequest) {
        Long studentId = jwtUtil.getUserIdFromToken(httpRequest.getHeader("token"));
        leaveRequestService.submitLeaveRequest(
            studentId, request.getClass_id(), request.getReason(),
            request.getStart_time(), request.getEnd_time(),
            request.getApprover_id()
        );
        return Result.success();
    }

    /**
     * 查询我的请假申请
     */
    @GetMapping("/leave-requests")
    public Result<PageResult<LeaveRequest>> listMyLeaveRequests(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long studentId = jwtUtil.getUserIdFromToken(request.getHeader("token"));
        PageResult<LeaveRequest> result = leaveRequestService.listMyLeaveRequests(studentId, page, pageSize);
        return Result.success(result);
    }

    @Data
    public static class LeaveRequestCreateRequest {
        private Long class_id;
        private String reason;
        private LocalDateTime start_time;
        private LocalDateTime end_time;
        private Long approver_id;
    }
}

