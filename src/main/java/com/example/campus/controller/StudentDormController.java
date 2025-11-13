package com.example.campus.controller;

import com.example.campus.common.Result;
import com.example.campus.entity.DormBed;
import com.example.campus.entity.DormRoom;
import com.example.campus.entity.DormStatusRecord;
import com.example.campus.entity.DormSurvey;
import com.example.campus.service.DormBedService;
import com.example.campus.service.DormRoomService;
import com.example.campus.service.DormStatusRecordService;
import com.example.campus.service.DormSurveyService;
import com.example.campus.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生宿舍相关控制器
 */
@RestController
@RequestMapping("/student/dorm")
@RequiredArgsConstructor
public class StudentDormController {
    private final DormRoomService dormRoomService;
    private final DormBedService dormBedService;
    private final DormStatusRecordService dormStatusRecordService;
    private final DormSurveyService dormSurveyService;
    private final JwtUtil jwtUtil;

    @GetMapping("/available-rooms")
    public Result<List<DormRoom>> getAvailableRooms(
            @RequestParam(required = false) Integer building_id,
            @RequestHeader("token") String token) {
        // 获取学生性别信息（简化处理，实际应从学生资料表获取）
        // Long userId = jwtUtil.getUserIdFromToken(token);
        Integer genderLimit = null; // 这里应该根据学生性别设置
        List<DormRoom> rooms = dormRoomService.getAvailableRooms(building_id, genderLimit);
        return Result.success(rooms);
    }

    @GetMapping("/my-bed")
    public Result<DormBed> getMyBed(@RequestHeader("token") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        DormBed bed = dormBedService.getBedByResident(userId);
        return Result.success(bed);
    }

    @PostMapping("/status-record")
    public Result<Void> createStatusRecord(@RequestBody DormStatusRecord record,
                                            @RequestHeader("token") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        record.setStudentId(userId);
        record.setOperatorId(userId);
        dormStatusRecordService.createRecord(record);
        return Result.success();
    }

    @PostMapping("/survey")
    public Result<Void> submitSurvey(@RequestBody DormSurvey survey,
                                    @RequestHeader("token") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        survey.setStudentId(userId);
        dormSurveyService.submitSurvey(survey);
        return Result.success();
    }

    @GetMapping("/survey")
    public Result<DormSurvey> getMySurvey(@RequestHeader("token") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        DormSurvey survey = dormSurveyService.getSurveyByStudent(userId);
        return Result.success(survey);
    }
}

