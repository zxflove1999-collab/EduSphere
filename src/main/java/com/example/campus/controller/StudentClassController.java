package com.example.campus.controller;

import com.example.campus.common.PageResult;
import com.example.campus.common.Result;
import com.example.campus.entity.Class;
import com.example.campus.entity.ClassSchedule;
import com.example.campus.entity.StudentClass;
import com.example.campus.service.StudentClassService;
import com.example.campus.util.JwtUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 学生选课控制器
 */
@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentClassController {
    private final StudentClassService studentClassService;
    private final JwtUtil jwtUtil;

    /**
     * 可选班级列表查询
     */
    @GetMapping("/classes/available")
    public Result<PageResult<Class>> listAvailableClasses(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String course_name,
            @RequestParam(required = false) String teacher_name,
            @RequestParam(required = false) Integer department_id) {
        PageResult<Class> result = studentClassService.listAvailableClasses(page, pageSize, course_name, teacher_name, department_id);
        return Result.success(result);
    }

    /**
     * 学生选择课程
     */
    @PostMapping("/my-classes")
    public Result<Void> selectClass(@RequestBody ClassSelectRequest request, HttpServletRequest httpRequest) {
        Long studentId = jwtUtil.getUserIdFromToken(httpRequest.getHeader("token"));
        studentClassService.selectClass(studentId, request.getClass_id());
        return Result.success();
    }

    /**
     * 学生删除课程 (退课)
     */
    @DeleteMapping("/my-classes/{class_id}")
    public Result<Void> dropClass(@PathVariable("class_id") Long classId, HttpServletRequest request) {
        Long studentId = jwtUtil.getUserIdFromToken(request.getHeader("token"));
        studentClassService.dropClass(studentId, classId);
        return Result.success();
    }

    /**
     * 学生已选课程列表
     */
    @GetMapping("/my-classes")
    public Result<List<StudentClass>> listMyClasses(
            @RequestParam(required = false) String semester,
            HttpServletRequest request) {
        Long studentId = jwtUtil.getUserIdFromToken(request.getHeader("token"));
        List<StudentClass> result = studentClassService.listMyClasses(studentId, semester);
        return Result.success(result);
    }

    /**
     * 学生查询课表
     */
    @GetMapping("/my-schedule")
    public Result<List<ClassSchedule>> getMySchedule(
            @RequestParam(required = false) String semester,
            HttpServletRequest request) {
        Long studentId = jwtUtil.getUserIdFromToken(request.getHeader("token"));
        List<ClassSchedule> result = studentClassService.getMySchedule(studentId, semester);
        return Result.success(result);
    }

    @Data
    public static class ClassSelectRequest {
        private Long class_id;
    }
}

