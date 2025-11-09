package com.example.campus.controller;

import com.example.campus.common.PageResult;
import com.example.campus.common.Result;
import com.example.campus.entity.Department;
import com.example.campus.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 院系管理控制器
 */
@RestController
@RequestMapping("/admin/depts")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    /**
     * 院系列表查询
     */
    @GetMapping
    public Result<PageResult<Department>> listDepartments(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String department_name) {
        PageResult<Department> result = departmentService.listDepartments(page, pageSize, department_name);
        return Result.success(result);
    }

    /**
     * 添加院系
     */
    @PostMapping
    public Result<Void> addDepartment(@RequestBody Department department) {
        departmentService.addDepartment(department);
        return Result.success();
    }

    /**
     * 根据ID查询院系
     */
    @GetMapping("/{id}")
    public Result<Department> getDepartmentById(@PathVariable("id") Integer id) {
        Department department = departmentService.getDepartmentById(id);
        return Result.success(department);
    }

    /**
     * 修改院系
     */
    @PutMapping
    public Result<Void> updateDepartment(@RequestBody Department department) {
        departmentService.updateDepartment(department);
        return Result.success();
    }

    /**
     * 删除院系
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteDepartment(@PathVariable("id") Integer id) {
        departmentService.deleteDepartment(id);
        return Result.success();
    }

    /**
     * 查询所有院系 (下拉列表)
     */
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> listAllDepartments() {
        List<Map<String, Object>> list = departmentService.listAllDepartments();
        return Result.success(list);
    }
}

