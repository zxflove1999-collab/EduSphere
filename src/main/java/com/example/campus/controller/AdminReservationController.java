package com.example.campus.controller;

import com.example.campus.common.PageResult;
import com.example.campus.common.Result;
import com.example.campus.entity.ReservationRequest;
import com.example.campus.service.ReservationService;
import com.example.campus.util.JwtUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 教室预约管理控制器 (管理员)
 */
@RestController
@RequestMapping("/admin/reservations")
@RequiredArgsConstructor
public class AdminReservationController {
    private final ReservationService reservationService;
    private final JwtUtil jwtUtil;

    /**
     * 查询待审批的预约申请
     */
    @GetMapping("/pending")
    public Result<PageResult<ReservationRequest>> listPendingReservations(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        PageResult<ReservationRequest> result = reservationService.listPendingReservations(page, pageSize);
        return Result.success(result);
    }

    /**
     * 审批预约申请
     */
    @PutMapping("/{request_id}/approve")
    public Result<Void> approveReservation(
            @PathVariable("request_id") Long requestId,
            @RequestBody ReservationApproveRequest request,
            HttpServletRequest httpRequest) {
        Long approverId = jwtUtil.getUserIdFromToken(httpRequest.getHeader("token"));
        reservationService.approveReservation(requestId, request.getStatus(), request.getApproval_remarks(), approverId);
        return Result.success();
    }

    @Data
    public static class ReservationApproveRequest {
        private Integer status;
        private String approval_remarks;
    }
}

