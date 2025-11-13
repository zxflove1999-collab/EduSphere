package com.example.campus.controller;

import com.example.campus.common.PageResult;
import com.example.campus.common.Result;
import com.example.campus.entity.DormStatusRecord;
import com.example.campus.service.DormStatusRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 宿舍状态记录控制器
 */
@RestController
@RequestMapping("/admin/dorm-status-records")
@RequiredArgsConstructor
public class DormStatusRecordController {
    private final DormStatusRecordService dormStatusRecordService;

    @GetMapping
    public Result<PageResult<DormStatusRecord>> listRecords(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long student_id,
            @RequestParam(required = false) Integer status_type) {
        PageResult<DormStatusRecord> result = dormStatusRecordService.listRecords(page, pageSize, student_id, status_type);
        return Result.success(result);
    }

    @PostMapping
    public Result<Void> createRecord(@RequestBody DormStatusRecord record,
                                     @RequestHeader("token") String token) {
        // 从token获取操作人ID（简化处理）
        Long operatorId = 1L; // 实际应从token解析
        record.setOperatorId(operatorId);
        dormStatusRecordService.createRecord(record);
        return Result.success();
    }

    @PutMapping("/{record_id}/end")
    public Result<Void> endRecord(@PathVariable("record_id") Long recordId) {
        dormStatusRecordService.endRecord(recordId, LocalDateTime.now());
        return Result.success();
    }
}

