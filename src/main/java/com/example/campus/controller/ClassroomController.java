package com.example.campus.controller;

import com.example.campus.common.PageResult;
import com.example.campus.common.Result;
import com.example.campus.entity.Classroom;
import com.example.campus.service.ClassroomService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教室管理控制器
 */
@RestController
@RequestMapping("/admin/classrooms")
@RequiredArgsConstructor
public class ClassroomController {
    private final ClassroomService classroomService;

    /**
     * 教室信息列表查询
     */
    @GetMapping
    public Result<PageResult<java.util.Map<String, Object>>> listClassrooms(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer building_id,
            @RequestParam(required = false) String room_number,
            @RequestParam(required = false) Integer is_available) {
        PageResult<java.util.Map<String, Object>> result = classroomService.listClassrooms(page, pageSize, building_id, room_number, is_available);
        return Result.success(result);
    }

    /**
     * 设置教室信息 (新增/修改)
     */
    @PostMapping
    public Result<Void> addClassroom(@RequestBody ClassroomCreateRequest request) {
        Classroom classroom = new Classroom();
        classroom.setBuildingId(request.getBuilding_id());
        classroom.setRoomNumber(request.getRoom_number());
        classroom.setCapacity(request.getCapacity());
        classroom.setRoomType(request.getRoom_type());
        classroom.setEquipment(request.getEquipment() != null ? String.join(",", request.getEquipment()) : null);
        classroom.setIsAvailable(1);
        classroomService.saveClassroom(classroom);
        return Result.success();
    }

    /**
     * 修改教室信息
     */
    @PutMapping
    public Result<Void> updateClassroom(@RequestBody ClassroomUpdateRequest request) {
        Classroom classroom = new Classroom();
        classroom.setRoomId(request.getRoom_id());
        classroom.setBuildingId(request.getBuilding_id());
        classroom.setRoomNumber(request.getRoom_number());
        classroom.setCapacity(request.getCapacity());
        classroom.setRoomType(request.getRoom_type());
        classroom.setEquipment(request.getEquipment() != null ? String.join(",", request.getEquipment()) : null);
        classroomService.saveClassroom(classroom);
        return Result.success();
    }

    /**
     * 设置停用教室
     */
    @PutMapping("/{room_id}/status")
    public Result<Void> updateClassroomStatus(
            @PathVariable("room_id") Long roomId,
            @RequestBody ClassroomStatusRequest request) {
        classroomService.updateClassroomStatus(roomId, request.getIs_available());
        return Result.success();
    }

    @Data
    public static class ClassroomCreateRequest {
        private Integer building_id;
        private String room_number;
        private Integer capacity;
        private String room_type;
        private List<String> equipment;
    }

    @Data
    public static class ClassroomUpdateRequest {
        private Long room_id;
        private Integer building_id;
        private String room_number;
        private Integer capacity;
        private String room_type;
        private List<String> equipment;
    }

    @Data
    public static class ClassroomStatusRequest {
        private Integer is_available;
        private String reason;
    }
}

