package com.example.campus.controller;

import com.example.campus.common.PageResult;
import com.example.campus.common.Result;
import com.example.campus.entity.Homework;
import com.example.campus.entity.SubmissionAnalysis;
import com.example.campus.service.HomeworkService;
import com.example.campus.util.JwtUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 作业管理控制器 (学生)
 */
@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentHomeworkController {
    private final HomeworkService homeworkService;
    private final JwtUtil jwtUtil;

    /**
     * 查询班级作业列表
     */
    @GetMapping("/classes/{class_id}/homeworks")
    public Result<PageResult<Homework>> listHomeworks(
            @PathVariable("class_id") Long classId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long studentId = jwtUtil.getUserIdFromToken(request.getHeader("token"));
        PageResult<Homework> result = homeworkService.listHomeworksForStudent(classId, studentId, page, pageSize);
        return Result.success(result);
    }

    /**
     * 查询作业详情
     */
    @GetMapping("/homeworks/{homework_id}")
    public Result<Map<String, Object>> getHomeworkDetail(
            @PathVariable("homework_id") Long homeworkId,
            HttpServletRequest request) {
        Long studentId = jwtUtil.getUserIdFromToken(request.getHeader("token"));
        Map<String, Object> result = homeworkService.getHomeworkDetailForStudent(homeworkId, studentId);
        return Result.success(result);
    }

    /**
     * 提交作业
     */
    @PostMapping("/homeworks/{homework_id}/submit")
    public Result<Map<String, Object>> submitHomework(
            @PathVariable("homework_id") Long homeworkId,
            @RequestBody HomeworkSubmitRequest request,
            HttpServletRequest httpRequest) {
        Long studentId = jwtUtil.getUserIdFromToken(httpRequest.getHeader("token"));
        Long submissionId = homeworkService.submitHomework(homeworkId, studentId, request.getContent(), request.getFile_urls());
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("submission_id", submissionId);
        return Result.success(result);
    }

    /**
     * 获取AI学情分析
     */
    @GetMapping("/submissions/{submission_id}/analysis")
    public Result<SubmissionAnalysis> getSubmissionAnalysis(@PathVariable("submission_id") Long submissionId) {
        SubmissionAnalysis result = homeworkService.getSubmissionAnalysis(submissionId);
        return Result.success(result);
    }

    @Data
    public static class HomeworkSubmitRequest {
        private String content;
        private List<String> file_urls;
    }
}

