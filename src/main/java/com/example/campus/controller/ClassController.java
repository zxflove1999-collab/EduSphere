package com.example.campus.controller;

import com.example.campus.common.PageResult;
import com.example.campus.common.Result;
import com.example.campus.entity.Class;
import com.example.campus.entity.ClassSchedule;
import com.example.campus.service.ClassService;
import com.example.campus.util.JwtUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 开课班级管理控制器
 */
@RestController
@RequestMapping("/teacher/classes")
@RequiredArgsConstructor
public class ClassController {
    private final ClassService classService;
    private final JwtUtil jwtUtil;

    /**
     * 教师的班级列表查询
     */
    @GetMapping
    public Result<PageResult<Class>> listClasses(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String course_name,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        String token = request.getHeader("token");
        if (token == null || token.trim().isEmpty()) {
            return Result.error("缺少token，请先登录");
        }
        Long teacherId = jwtUtil.getUserIdFromToken(token);
        if (teacherId == null) {
            return Result.error("token无效，请重新登录");
        }
        PageResult<Class> result = classService.listClassesByTeacher(teacherId, page, pageSize, course_name, semester, status);
        return Result.success(result);
    }

    /**
     * 创建班级
     */
    @PostMapping
    public Result<Void> createClass(@RequestBody ClassCreateRequest request, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("token");
        if (token == null || token.trim().isEmpty()) {
            return Result.error("缺少token，请先登录");
        }
        Long teacherId = jwtUtil.getUserIdFromToken(token);
        if (teacherId == null) {
            return Result.error("token无效，请重新登录");
        }
        classService.createClass(request.getClazz(), request.getSchedules(), teacherId);
        return Result.success();
    }

    /**
     * 根据ID查询班级详情
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getClassById(@PathVariable("id") Long id) {
        Map<String, Object> clazz = classService.getClassById(id);
        return Result.success(clazz);
    }

    /**
     * 修改班级信息
     */
    @PutMapping
    public Result<Void> updateClass(@RequestBody ClassUpdateRequest request) {
        classService.updateClass(request.getClazz(), request.getSchedules());
        return Result.success();
    }

    /**
     * 删除班级
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteClass(@PathVariable("id") Long id) {
        classService.deleteClass(id);
        return Result.success();
    }

    @Data
    public static class ClassCreateRequest {
        private Class clazz;
        private List<ClassSchedule> schedules;
    }

    @Data
    public static class ClassUpdateRequest {
        private Class clazz;
        private List<ClassSchedule> schedules;
    }
}

