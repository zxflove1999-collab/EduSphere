package com.example.campus.controller;

import com.example.campus.common.PageResult;
import com.example.campus.common.Result;
import com.example.campus.entity.AttendanceRecord;
import com.example.campus.entity.AttendanceSession;
import com.example.campus.service.AttendanceService;
import com.example.campus.util.JwtUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 考勤管理控制器 (教师)
 */
@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;
    private final JwtUtil jwtUtil;

    /**
     * 发起签到
     */
    @PostMapping("/classes/{class_id}/attendance/start")
    public Result<Map<String, Object>> startAttendance(
            @PathVariable("class_id") Long classId,
            @RequestBody AttendanceStartRequest request,
            HttpServletRequest httpRequest) {
        Long userId = jwtUtil.getUserIdFromToken(httpRequest.getHeader("token"));
        AttendanceSession session = attendanceService.startAttendance(
            classId, request.getSession_type(), request.getDuration(),
            request.getRequired_latitude(), request.getRequired_longitude(),
            request.getRequired_radius(), userId
        );
        
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("session_id", session.getSessionId());
        result.put("start_time", session.getStartTime());
        result.put("end_time", session.getEndTime());
        result.put("status", session.getStatus());
        return Result.success(result);
    }

    /**
     * 结束签到
     */
    @PutMapping("/attendance/{session_id}/stop")
    public Result<Void> stopAttendance(@PathVariable("session_id") Long sessionId) {
        attendanceService.stopAttendance(sessionId);
        return Result.success();
    }

    /**
     * 查询班级考勤任务列表
     */
    @GetMapping("/classes/{class_id}/attendance")
    public Result<PageResult<AttendanceSession>> listAttendanceSessions(
            @PathVariable("class_id") Long classId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        PageResult<AttendanceSession> result = attendanceService.listAttendanceSessions(classId, page, pageSize);
        return Result.success(result);
    }

    /**
     * 查询考勤详情
     */
    @GetMapping("/attendance/{session_id}/records")
    public Result<List<AttendanceRecord>> getAttendanceRecords(
            @PathVariable("session_id") Long sessionId,
            @RequestParam(required = false) Integer status) {
        List<AttendanceRecord> result = attendanceService.getAttendanceRecords(sessionId, status);
        return Result.success(result);
    }

    /**
     * 手动修改考勤状态
     */
    @PutMapping("/attendance/records/{record_id}")
    public Result<Void> updateAttendanceRecord(
            @PathVariable("record_id") Long recordId,
            @RequestBody AttendanceRecordUpdateRequest request,
            HttpServletRequest httpRequest) {
        Long userId = jwtUtil.getUserIdFromToken(httpRequest.getHeader("token"));
        attendanceService.updateAttendanceRecord(recordId, request.getStatus(), request.getRemarks(), userId);
        return Result.success();
    }

    @Data
    public static class AttendanceStartRequest {
        private Integer session_type;
        private Integer duration;
        private BigDecimal required_latitude;
        private BigDecimal required_longitude;
        private Integer required_radius;
    }

    @Data
    public static class AttendanceRecordUpdateRequest {
        private Integer status;
        private String remarks;
    }
}

