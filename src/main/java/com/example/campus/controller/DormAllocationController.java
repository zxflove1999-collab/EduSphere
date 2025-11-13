package com.example.campus.controller;

import com.example.campus.common.Result;
import com.example.campus.dto.DormAllocationSuggestionRequest;
import com.example.campus.dto.DormBatchAllocationRequest;
import com.example.campus.service.DormAllocationService;
import com.example.campus.vo.DormAllocationSuggestionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 宿舍分配相关控制器
 */
@RestController
@RequestMapping("/admin/dorm/allocation")
@RequiredArgsConstructor
public class DormAllocationController {

    private final DormAllocationService dormAllocationService;

    /**
     * 批量分配/调整宿舍
     */
    @PostMapping("/batch")
    public Result<Void> batchAllocate(@RequestBody DormBatchAllocationRequest request) {
        dormAllocationService.batchAllocate(request);
        return Result.success();
    }

    /**
     * 生成宿舍分配建议
     */
    @PostMapping("/suggestions")
    public Result<DormAllocationSuggestionVO> generateSuggestions(@RequestBody DormAllocationSuggestionRequest request) {
        DormAllocationSuggestionVO vo = dormAllocationService.generateSuggestions(request);
        return Result.success(vo);
    }
}

