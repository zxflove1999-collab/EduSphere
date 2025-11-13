package com.example.campus.controller;

import com.example.campus.common.PageResult;
import com.example.campus.common.Result;
import com.example.campus.entity.Course;
import com.example.campus.service.CourseService;
import com.example.campus.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 课程模板管理控制器
 */
@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final JwtUtil jwtUtil;

    /**
     * 课程模板列表查询
     */
    @GetMapping
    public Result<PageResult<Course>> listCourses(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String course_name,
            @RequestParam(required = false) String course_code,
            @RequestParam(required = false) Integer department_id) {
        PageResult<Course> result = courseService.listCourses(page, pageSize, course_name, course_code, department_id);
        return Result.success(result);
    }

    /**
     * 添加课程模板
     */
    @PostMapping
    public Result<Void> addCourse(@RequestBody Course course, HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromToken(request.getHeader("token"));
        courseService.addCourse(course, userId);
        return Result.success();
    }

    /**
     * 根据ID查询课程模板
     */
    @GetMapping("/{id}")
    public Result<Course> getCourseById(@PathVariable("id") Long id) {
        Course course = courseService.getCourseById(id);
        return Result.success(course);
    }

    /**
     * 修改课程模板
     */
    @PutMapping
    public Result<Void> updateCourse(@RequestBody Course course) {
        courseService.updateCourse(course);
        return Result.success();
    }

    /**
     * 删除课程模板
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCourse(@PathVariable("id") Long id) {
        courseService.deleteCourse(id);
        return Result.success();
    }

    /**
     * 查询所有课程模板 (下拉列表)
     */
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> listAllCourses() {
        List<Map<String, Object>> list = courseService.listAllCourses();
        return Result.success(list);
    }
}

