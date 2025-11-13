package com.example.campus.controller;

import com.example.campus.common.PageResult;
import com.example.campus.common.Result;
import com.example.campus.dto.DormBuildingStatusUpdateRequest;
import com.example.campus.entity.DormBuilding;
import com.example.campus.service.DormBuildingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 宿舍楼管理控制器
 */
@RestController
@RequestMapping("/admin/dorm")
@RequiredArgsConstructor
public class DormBuildingController {
    private final DormBuildingService dormBuildingService;

    @GetMapping("/buildings")
    public Result<PageResult<DormBuilding>> listBuildings(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer gender_limit,
            @RequestParam(required = false) Integer is_active) {
        PageResult<DormBuilding> result = dormBuildingService.listBuildings(page, pageSize, gender_limit, is_active);
        return Result.success(result);
    }

    @PutMapping("/buildings/{building_id}/status")
    public Result<Void> updateBuildingStatus(@PathVariable("building_id") Integer buildingId,
                                             @RequestBody DormBuildingStatusUpdateRequest request) {
        dormBuildingService.updateBuildingStatus(buildingId, request.getIsActive());
        return Result.success();
    }
}

