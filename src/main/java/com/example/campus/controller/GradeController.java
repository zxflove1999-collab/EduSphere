package com.example.campus.controller;

import com.example.campus.common.Result;
import com.example.campus.entity.GradeComponent;
import com.example.campus.service.GradeService;
import com.example.campus.util.JwtUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 成绩管理控制器
 */
@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class GradeController {
    private final GradeService gradeService;
    private final JwtUtil jwtUtil;

    /**
     * 查询班级成绩组成
     */
    @GetMapping("/classes/{class_id}/grade-components")
    public Result<List<GradeComponent>> listGradeComponents(@PathVariable("class_id") Long classId) {
        List<GradeComponent> result = gradeService.listGradeComponents(classId);
        return Result.success(result);
    }

    /**
     * 添加成绩组成
     */
    @PostMapping("/classes/{class_id}/grade-components")
    public Result<Void> addGradeComponent(
            @PathVariable("class_id") Long classId,
            @RequestBody GradeComponent component) {
        component.setClassId(classId);
        gradeService.addGradeComponent(component);
        return Result.success();
    }

    /**
     * 修改成绩组成
     */
    @PutMapping("/classes/grade-components")
    public Result<Void> updateGradeComponent(@RequestBody GradeComponent component) {
        gradeService.updateGradeComponent(component);
        return Result.success();
    }

    /**
     * 删除成绩组成
     */
    @DeleteMapping("/classes/grade-components/{id}")
    public Result<Void> deleteGradeComponent(@PathVariable("id") Long id) {
        gradeService.deleteGradeComponent(id);
        return Result.success();
    }

    /**
     * 查询班级成绩册
     */
    @GetMapping("/classes/{class_id}/gradebook")
    public Result<Map<String, Object>> getGradebook(@PathVariable("class_id") Long classId) {
        Map<String, Object> result = gradeService.getGradebook(classId);
        return Result.success(result);
    }

    /**
     * 批量录入/修改学生成绩
     */
    @PostMapping("/grades/batch")
    public Result<Void> batchUpdateGrades(
            @RequestBody BatchGradeRequest request,
            HttpServletRequest httpRequest) {
        Long userId = jwtUtil.getUserIdFromToken(httpRequest.getHeader("token"));
        gradeService.batchUpdateGrades(request.getComponent_id(), request.getScores(), userId);
        return Result.success();
    }

    @Data
    public static class BatchGradeRequest {
        private Long component_id;
        private List<GradeService.GradeScore> scores;
    }
}

