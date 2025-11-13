package com.example.campus.controller;

import com.example.campus.common.PageResult;
import com.example.campus.common.Result;
import com.example.campus.entity.DormBuilding;
import com.example.campus.service.DormBuildingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 宿舍楼管理控制器
 */
@RestController
@RequestMapping("/admin/dorm-buildings")
@RequiredArgsConstructor
public class DormBuildingController {
    private final DormBuildingService dormBuildingService;

    @GetMapping
    public Result<PageResult<DormBuilding>> listBuildings(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String building_name,
            @RequestParam(required = false) Integer is_active) {
        PageResult<DormBuilding> result = dormBuildingService.listBuildings(page, pageSize, building_name, is_active);
        return Result.success(result);
    }

    @PostMapping
    public Result<Void> addBuilding(@RequestBody DormBuilding building) {
        dormBuildingService.saveBuilding(building);
        return Result.success();
    }

    @PutMapping
    public Result<Void> updateBuilding(@RequestBody DormBuilding building) {
        dormBuildingService.saveBuilding(building);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteBuilding(@PathVariable("id") Integer id) {
        dormBuildingService.deleteBuilding(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<DormBuilding> getBuilding(@PathVariable("id") Integer id) {
        DormBuilding building = dormBuildingService.getBuilding(id);
        return Result.success(building);
    }
}

