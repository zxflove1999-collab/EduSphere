package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.common.PageResult;
import com.example.campus.entity.ReservationRequest;
import com.example.campus.mapper.ClassroomMapper;
import com.example.campus.mapper.ReservationRequestMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 教室预约服务
 */
@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRequestMapper reservationRequestMapper;
    private final ClassroomMapper classroomMapper;

    /**
     * 查询可用教室
     */
    public List<java.util.Map<String, Object>> getAvailableRooms(LocalDateTime startTime, LocalDateTime endTime,
                                              Integer buildingId, Integer capacityMin, String equipmentRequired) {
        List<java.util.Map<String, Object>> rooms = classroomMapper.selectAvailableRooms(startTime, endTime, buildingId, capacityMin, equipmentRequired);
        
        // 转换equipment字段为数组
        for (java.util.Map<String, Object> room : rooms) {
            String equipment = (String) room.get("equipment");
            if (equipment != null && !equipment.isEmpty()) {
                room.put("equipment", java.util.Arrays.asList(equipment.split(",")));
            } else {
                room.put("equipment", java.util.List.of());
            }
        }
        
        return rooms;
    }

    /**
     * 提交预约申请
     */
    @Transactional
    public void submitReservation(ReservationRequest request, Long requesterId) {
        // 检查教室是否存在且可用
        com.example.campus.entity.Classroom classroom = classroomMapper.selectById(request.getRoomId());
        if (classroom == null) {
            throw new BusinessException("教室不存在");
        }
        if (classroom.getIsAvailable() != 1) {
            throw new BusinessException("教室已停用，无法预约");
        }
        
        // 检查时间冲突
        List<ReservationRequest> conflicts = reservationRequestMapper.selectByRoomIdAndTimeRange(
            request.getRoomId(), request.getStartTime(), request.getEndTime()
        );
        if (!conflicts.isEmpty()) {
            throw new BusinessException("该时间段已被预约");
        }
        
        request.setRequesterId(requesterId);
        request.setStatus(1); // 待审批
        reservationRequestMapper.insert(request);
    }

    /**
     * 查看我的预约记录
     */
    public PageResult<ReservationRequest> listMyReservations(Long requesterId, Integer page, Integer pageSize,
                                                             Integer status, java.time.LocalDate dateFrom) {
        PageHelper.startPage(page != null ? page : 1, pageSize != null ? pageSize : 10);
        List<ReservationRequest> requests = reservationRequestMapper.selectByRequesterId(requesterId, status, dateFrom);
        PageInfo<ReservationRequest> pageInfo = new PageInfo<>(requests);
        return new PageResult<>(pageInfo.getTotal(), requests);
    }

    /**
     * 撤回预约申请
     */
    @Transactional
    public void withdrawReservation(Long requestId, Long requesterId) {
        ReservationRequest request = reservationRequestMapper.selectById(requestId);
        if (request == null) {
            throw new BusinessException("预约申请不存在");
        }
        if (!request.getRequesterId().equals(requesterId)) {
            throw new BusinessException("无权撤回该预约申请");
        }
        if (request.getStatus() != 1) {
            throw new BusinessException("只能撤回待审批的预约申请");
        }
        reservationRequestMapper.updateStatus(requestId, 4); // 已撤回
    }

    /**
     * 查询待审批的预约申请 (管理员)
     */
    public PageResult<ReservationRequest> listPendingReservations(Integer page, Integer pageSize) {
        PageHelper.startPage(page != null ? page : 1, pageSize != null ? pageSize : 10);
        List<ReservationRequest> requests = reservationRequestMapper.selectPending();
        PageInfo<ReservationRequest> pageInfo = new PageInfo<>(requests);
        return new PageResult<>(pageInfo.getTotal(), requests);
    }

    /**
     * 审批预约申请 (管理员)
     */
    @Transactional
    public void approveReservation(Long requestId, Integer status, String approvalRemarks, Long approverId) {
        ReservationRequest request = reservationRequestMapper.selectById(requestId);
        if (request == null) {
            throw new BusinessException("预约申请不存在");
        }
        if (request.getStatus() != 1) {
            throw new BusinessException("该预约申请已处理");
        }
        
        request.setStatus(status);
        request.setApproverId(approverId);
        request.setApprovalRemarks(approvalRemarks);
        reservationRequestMapper.update(request);
    }
}

