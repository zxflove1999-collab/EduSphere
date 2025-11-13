package com.example.campus.controller;

import com.example.campus.common.PageResult;
import com.example.campus.common.Result;
import com.example.campus.dto.DormRoomSaveRequest;
import com.example.campus.service.DormRoomService;
import com.example.campus.vo.DormRoomOverviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 宿舍房间管理控制器
 */
@RestController
@RequestMapping("/admin/dorm")
@RequiredArgsConstructor
public class DormRoomController {
    private final DormRoomService dormRoomService;

    @GetMapping("/rooms")
    public Result<PageResult<DormRoomOverviewVO>> listRooms(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer building_id,
            @RequestParam(required = false) String room_number,
            @RequestParam(required = false) Integer occupancy_status) {
        PageResult<DormRoomOverviewVO> result = dormRoomService.listRooms(page, pageSize, building_id, room_number, occupancy_status);
        return Result.success(result);
    }

    @PostMapping("/rooms")
    public Result<Void> addRoom(@RequestBody DormRoomSaveRequest request) {
        dormRoomService.saveRoom(request);
        return Result.success();
    }

    @PutMapping("/rooms")
    public Result<Void> updateRoom(@RequestBody DormRoomSaveRequest request) {
        dormRoomService.saveRoom(request);
        return Result.success();
    }

}

