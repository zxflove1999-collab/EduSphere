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
import java.util.Map;

/**
 * 考勤控制器 (学生)
 */
@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentAttendanceController {
    private final AttendanceService attendanceService;
    private final JwtUtil jwtUtil;

    /**
     * 查询当前可签到任务
     */
    @GetMapping("/classes/{class_id}/attendance/active")
    public Result<AttendanceSession> getActiveAttendance(@PathVariable("class_id") Long classId) {
        AttendanceSession result = attendanceService.getActiveAttendance(classId);
        if (result == null) {
            return Result.error("当前没有进行中的签到任务");
        }
        return Result.success(result);
    }

    /**
     * 课程签到
     */
    @PostMapping("/attendance/{session_id}/check-in")
    public Result<Map<String, Object>> checkIn(
            @PathVariable("session_id") Long sessionId,
            @RequestBody CheckInRequest request,
            HttpServletRequest httpRequest) {
        Long studentId = jwtUtil.getUserIdFromToken(httpRequest.getHeader("token"));
        AttendanceRecord record = attendanceService.checkIn(
            sessionId, studentId,
            request.getCheck_in_latitude(), request.getCheck_in_longitude(),
            request.getFace_image_url(), request.getFace_api_score()
        );
        
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("record_id", record.getRecordId());
        result.put("status", record.getStatus());
        result.put("check_in_time", record.getCheckInTime());
        result.put("face_api_score", record.getFaceApiScore());
        return Result.success(result);
    }

    /**
     * 查询我的考勤记录
     */
    @GetMapping("/classes/{class_id}/my-attendance")
    public Result<PageResult<AttendanceRecord>> getMyAttendanceRecords(
            @PathVariable("class_id") Long classId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long studentId = jwtUtil.getUserIdFromToken(request.getHeader("token"));
        PageResult<AttendanceRecord> result = attendanceService.getMyAttendanceRecords(studentId, classId, page, pageSize);
        return Result.success(result);
    }

    @Data
    public static class CheckInRequest {
        private BigDecimal check_in_latitude;
        private BigDecimal check_in_longitude;
        private String face_image_url;
        private BigDecimal face_api_score;
    }
}

