package com.example.campus.controller;

import com.example.campus.common.PageResult;
import com.example.campus.common.Result;
import com.example.campus.entity.DormRoom;
import com.example.campus.service.DormRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 宿舍房间管理控制器
 */
@RestController
@RequestMapping("/admin/dorm-rooms")
@RequiredArgsConstructor
public class DormRoomController {
    private final DormRoomService dormRoomService;

    @GetMapping
    public Result<PageResult<DormRoom>> listRooms(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer building_id,
            @RequestParam(required = false) String room_number,
            @RequestParam(required = false) Integer room_status) {
        PageResult<DormRoom> result = dormRoomService.listRooms(page, pageSize, building_id, room_number, room_status);
        return Result.success(result);
    }

    @PostMapping
    public Result<Void> addRoom(@RequestBody DormRoom room) {
        dormRoomService.saveRoom(room);
        return Result.success();
    }

    @PutMapping
    public Result<Void> updateRoom(@RequestBody DormRoom room) {
        dormRoomService.saveRoom(room);
        return Result.success();
    }

    @PutMapping("/{room_id}/status")
    public Result<Void> updateRoomStatus(@PathVariable("room_id") Long roomId,
                                         @RequestParam("room_status") Integer roomStatus) {
        dormRoomService.updateRoomStatus(roomId, roomStatus);
        return Result.success();
    }

    @GetMapping("/{room_id}")
    public Result<DormRoom> getRoom(@PathVariable("room_id") Long roomId) {
        DormRoom room = dormRoomService.getRoom(roomId);
        return Result.success(room);
    }
}

