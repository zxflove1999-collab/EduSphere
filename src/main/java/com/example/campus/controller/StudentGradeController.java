package com.example.campus.controller;

import com.example.campus.common.Result;
import com.example.campus.service.GradeService;
import com.example.campus.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 学生成绩查询控制器
 */
@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentGradeController {
    private final GradeService gradeService;
    private final JwtUtil jwtUtil;

    /**
     * 查询我的成绩
     */
    @GetMapping("/my-classes/{class_id}/grades")
    public Result<Map<String, Object>> getMyGrades(
            @PathVariable("class_id") Long classId,
            HttpServletRequest request) {
        Long studentId = jwtUtil.getUserIdFromToken(request.getHeader("token"));
        Map<String, Object> result = gradeService.getMyGrades(studentId, classId);
        return Result.success(result);
    }
}

