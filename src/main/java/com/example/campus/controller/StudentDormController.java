package com.example.campus.controller;

import com.example.campus.common.Result;
import com.example.campus.dto.DormPreferenceRequest;
import com.example.campus.dto.DormSurveyRequest;
import com.example.campus.entity.DormBed;
import com.example.campus.entity.DormPreference;
import com.example.campus.entity.DormSurvey;
import com.example.campus.service.DormBedService;
import com.example.campus.service.DormPreferenceService;
import com.example.campus.service.DormSurveyService;
import com.example.campus.service.DormRoomService;
import com.example.campus.util.JwtUtil;
import com.example.campus.vo.DormOccupantVO;
import com.example.campus.vo.DormRoomOverviewVO;
import com.example.campus.vo.MyDormInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 学生宿舍相关控制器
 */
@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentDormController {
    private final DormPreferenceService dormPreferenceService;
    private final DormSurveyService dormSurveyService;
    private final DormBedService dormBedService;
    private final DormRoomService dormRoomService;
    private final JwtUtil jwtUtil;

    /**
     * 提交宿舍偏好问卷
     */
    @PostMapping("/dorm/preferences")
    public Result<Void> submitPreferences(@RequestBody DormPreferenceRequest request,
                                          @RequestHeader("token") String token) {
        Long studentId = jwtUtil.getUserIdFromToken(token);
        dormPreferenceService.submitPreference(studentId, request);
        return Result.success();
    }

    /**
     * 提交宿舍问卷 (t_dorm_survey)
     */
    @PostMapping("/dorm/survey")
    public Result<Void> submitDormSurvey(@RequestBody DormSurveyRequest request,
                                         @RequestHeader("token") String token) {
        Long studentId = jwtUtil.getUserIdFromToken(token);
        dormSurveyService.submitSurvey(studentId, request);
        return Result.success();
    }

    /**
     * 查询本人最新的宿舍问卷
     */
    @GetMapping("/dorm/survey")
    public Result<DormSurvey> getDormSurvey(@RequestHeader("token") String token) {
        Long studentId = jwtUtil.getUserIdFromToken(token);
        DormSurvey survey = dormSurveyService.getSurveyByStudent(studentId);
        return Result.success(survey);
    }

    /**
     * 查询我的宿舍信息
     */
    @GetMapping("/my-dorm")
    public Result<MyDormInfoVO> getMyDormInfo(@RequestHeader("token") String token) {
        Long studentId = jwtUtil.getUserIdFromToken(token);
        DormBed bed = dormBedService.getBedByResident(studentId);
        if (bed == null) {
            return Result.success(null);
        }
        DormRoomOverviewVO overview = dormRoomService.getRoomOverview(bed.getRoomId());
        if (overview == null) {
            return Result.success(null);
        }
        List<DormOccupantVO> roommates = overview.getCurrentOccupants().stream()
                .filter(occupant -> occupant.getStudentId() == null || !occupant.getStudentId().equals(studentId))
                .collect(Collectors.toList());

        DormPreference preference = dormPreferenceService.getPreferenceByStudent(studentId);

        MyDormInfoVO vo = new MyDormInfoVO();
        vo.setRoomId(overview.getRoomId());
        vo.setFullName(overview.getFullName());
        vo.setBedNumber(bed.getBedNumber());
        vo.setRoommates(roommates);
        vo.setMatchingScore(preference != null ? preference.getMatchingScore() : null);
        return Result.success(vo);
    }
}