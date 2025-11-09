package com.example.campus.controller;

import com.example.campus.common.PageResult;
import com.example.campus.common.Result;
import com.example.campus.entity.Building;
import com.example.campus.service.BuildingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 教学楼管理控制器
 */
@RestController
@RequestMapping("/admin/buildings")
@RequiredArgsConstructor
public class BuildingController {
    private final BuildingService buildingService;

    /**
     * 教学楼列表查询
     */
    @GetMapping
    public Result<PageResult<Building>> listBuildings(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String building_name,
            @RequestParam(required = false) Integer is_active) {
        PageResult<Building> result = buildingService.listBuildings(page, pageSize, building_name, is_active);
        return Result.success(result);
    }

    /**
     * 添加教学楼
     */
    @PostMapping
    public Result<Void> addBuilding(@RequestBody Building building) {
        buildingService.addBuilding(building);
        return Result.success();
    }

    /**
     * 修改教学楼
     */
    @PutMapping
    public Result<Void> updateBuilding(@RequestBody Building building) {
        buildingService.updateBuilding(building);
        return Result.success();
    }

    /**
     * 删除教学楼
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteBuilding(@PathVariable("id") Integer id) {
        buildingService.deleteBuilding(id);
        return Result.success();
    }
}

