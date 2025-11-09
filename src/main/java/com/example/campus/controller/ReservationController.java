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
import java.time.LocalDateTime;
import java.util.List;

/**
 * 教室预约控制器
 */
@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final JwtUtil jwtUtil;

    /**
     * 查询可用教室
     */
    @GetMapping("/available-rooms")
    public Result<List<java.util.Map<String, Object>>> getAvailableRooms(
            @RequestParam("start_time") LocalDateTime startTime,
            @RequestParam("end_time") LocalDateTime endTime,
            @RequestParam(required = false) Integer building_id,
            @RequestParam(required = false) Integer capacity_min,
            @RequestParam(required = false) String equipment_required) {
        List<java.util.Map<String, Object>> result = reservationService.getAvailableRooms(startTime, endTime, building_id, capacity_min, equipment_required);
        return Result.success(result);
    }

    /**
     * 提交预约申请
     */
    @PostMapping
    public Result<Void> submitReservation(
            @RequestBody ReservationCreateRequest request,
            HttpServletRequest httpRequest) {
        Long requesterId = jwtUtil.getUserIdFromToken(httpRequest.getHeader("token"));
        ReservationRequest reservationRequest = new ReservationRequest();
        reservationRequest.setRoomId(request.getRoom_id());
        reservationRequest.setStartTime(request.getStart_time());
        reservationRequest.setEndTime(request.getEnd_time());
        reservationRequest.setPurpose(request.getPurpose());
        reservationRequest.setParticipantsCount(request.getParticipants_count());
        reservationService.submitReservation(reservationRequest, requesterId);
        return Result.success();
    }

    /**
     * 查看我的预约记录
     */
    @GetMapping("/my")
    public Result<PageResult<ReservationRequest>> listMyReservations(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) java.time.LocalDate date_from,
            HttpServletRequest request) {
        Long requesterId = jwtUtil.getUserIdFromToken(request.getHeader("token"));
        PageResult<ReservationRequest> result = reservationService.listMyReservations(requesterId, page, pageSize, status, date_from);
        return Result.success(result);
    }

    /**
     * 撤回预约申请
     */
    @PutMapping("/{request_id}/withdraw")
    public Result<Void> withdrawReservation(
            @PathVariable("request_id") Long requestId,
            HttpServletRequest request) {
        Long requesterId = jwtUtil.getUserIdFromToken(request.getHeader("token"));
        reservationService.withdrawReservation(requestId, requesterId);
        return Result.success();
    }

    @Data
    public static class ReservationCreateRequest {
        private Long room_id;
        private LocalDateTime start_time;
        private LocalDateTime end_time;
        private String purpose;
        private Integer participants_count;
    }
}

