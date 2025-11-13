package com.example.campus.controller;

import com.example.campus.common.Result;
import com.example.campus.entity.DormBed;
import com.example.campus.service.DormBedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 宿舍床位管理控制器
 */
@RestController
@RequestMapping("/admin/dorm-beds")
@RequiredArgsConstructor
public class DormBedController {
    private final DormBedService dormBedService;

    @GetMapping("/room/{room_id}")
    public Result<List<DormBed>> getBedsByRoom(@PathVariable("room_id") Long roomId) {
        List<DormBed> beds = dormBedService.getBedsByRoom(roomId);
        return Result.success(beds);
    }

    @GetMapping("/room/{room_id}/available")
    public Result<List<DormBed>> getAvailableBeds(@PathVariable("room_id") Long roomId) {
        List<DormBed> beds = dormBedService.getAvailableBeds(roomId);
        return Result.success(beds);
    }

    @PostMapping
    public Result<Void> addBed(@RequestBody DormBed bed) {
        dormBedService.saveBed(bed);
        return Result.success();
    }

    @PutMapping
    public Result<Void> updateBed(@RequestBody DormBed bed) {
        dormBedService.saveBed(bed);
        return Result.success();
    }

    @PutMapping("/{bed_id}/allocate")
    public Result<Void> allocateBed(@PathVariable("bed_id") Long bedId,
                                     @RequestParam("student_id") Long studentId) {
        dormBedService.allocateBed(bedId, studentId);
        return Result.success();
    }

    @PutMapping("/{bed_id}/release")
    public Result<Void> releaseBed(@PathVariable("bed_id") Long bedId) {
        dormBedService.releaseBed(bedId);
        return Result.success();
    }
}

