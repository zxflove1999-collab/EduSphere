package com.example.campus.controller;

import com.example.campus.common.PageResult;
import com.example.campus.common.Result;
import com.example.campus.entity.Homework;
import com.example.campus.entity.HomeworkSubmission;
import com.example.campus.entity.TeachingSuggestion;
import com.example.campus.service.HomeworkService;
import com.example.campus.util.JwtUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * 作业管理控制器 (教师)
 */
@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class HomeworkController {
    private final HomeworkService homeworkService;
    private final JwtUtil jwtUtil;

    /**
     * 查询班级作业列表
     */
    @GetMapping("/classes/{class_id}/homeworks")
    public Result<PageResult<Homework>> listHomeworks(
            @PathVariable("class_id") Long classId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        PageResult<Homework> result = homeworkService.listHomeworksByClass(classId, page, pageSize);
        return Result.success(result);
    }

    /**
     * 发布作业
     */
    @PostMapping("/classes/{class_id}/homeworks")
    public Result<Void> publishHomework(
            @PathVariable("class_id") Long classId,
            @RequestBody HomeworkCreateRequest request,
            HttpServletRequest httpRequest) {
        Long userId = jwtUtil.getUserIdFromToken(httpRequest.getHeader("token"));
        Homework homework = new Homework();
        homework.setClassId(classId);
        homework.setTitle(request.getTitle());
        homework.setDescription(request.getDescription());
        homework.setDueDate(request.getDue_date());
        homework.setAllowLateSubmission(request.getAllow_late_submission());
        homeworkService.publishHomework(homework, userId);
        return Result.success();
    }

    /**
     * 查询作业详情
     */
    @GetMapping("/homeworks/{homework_id}")
    public Result<Homework> getHomeworkById(@PathVariable("homework_id") Long homeworkId) {
        Homework homework = homeworkService.getHomeworkById(homeworkId);
        return Result.success(homework);
    }

    /**
     * 修改作业
     */
    @PutMapping("/homeworks")
    public Result<Void> updateHomework(@RequestBody HomeworkUpdateRequest request) {
        Homework homework = new Homework();
        homework.setHomeworkId(request.getHomework_id());
        homework.setTitle(request.getTitle());
        homework.setDescription(request.getDescription());
        homework.setDueDate(request.getDue_date());
        homework.setAllowLateSubmission(request.getAllow_late_submission());
        homeworkService.updateHomework(homework);
        return Result.success();
    }

    /**
     * 删除作业
     */
    @DeleteMapping("/homeworks/{homework_id}")
    public Result<Void> deleteHomework(@PathVariable("homework_id") Long homeworkId) {
        homeworkService.deleteHomework(homeworkId);
        return Result.success();
    }

    /**
     * 查询作业提交列表
     */
    @GetMapping("/homeworks/{homework_id}/submissions")
    public Result<PageResult<HomeworkSubmission>> listSubmissions(
            @PathVariable("homework_id") Long homeworkId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        PageResult<HomeworkSubmission> result = homeworkService.listSubmissions(homeworkId, page, pageSize, status);
        return Result.success(result);
    }

    /**
     * 批改作业
     */
    @PutMapping("/submissions/{submission_id}/grade")
    public Result<Void> gradeSubmission(
            @PathVariable("submission_id") Long submissionId,
            @RequestBody GradeSubmissionRequest request) {
        homeworkService.gradeSubmission(submissionId, request.getGrade(), request.getTeacher_feedback());
        return Result.success();
    }

    /**
     * 获取AI教学建议
     */
    @GetMapping("/homeworks/{homework_id}/suggestions")
    public Result<List<TeachingSuggestion>> getTeachingSuggestions(@PathVariable("homework_id") Long homeworkId) {
        List<TeachingSuggestion> result = homeworkService.getTeachingSuggestions(homeworkId);
        return Result.success(result);
    }

    @Data
    public static class HomeworkCreateRequest {
        private String title;
        private String description;
        private java.time.LocalDateTime due_date;
        private Integer allow_late_submission;
    }

    @Data
    public static class HomeworkUpdateRequest {
        private Long homework_id;
        private String title;
        private String description;
        private java.time.LocalDateTime due_date;
        private Integer allow_late_submission;
    }

    @Data
    public static class GradeSubmissionRequest {
        private BigDecimal grade;
        private String teacher_feedback;
    }
}

