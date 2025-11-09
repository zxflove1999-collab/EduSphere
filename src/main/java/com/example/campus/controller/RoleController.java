package com.example.campus.controller;

import com.example.campus.common.PageResult;
import com.example.campus.common.Result;
import com.example.campus.entity.Role;
import com.example.campus.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/admin/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    /**
     * 角色列表查询
     */
    @GetMapping
    public Result<PageResult<Role>> listRoles(@RequestParam(required = false) Integer page,
                                              @RequestParam(required = false) Integer pageSize,
                                              @RequestParam(required = false) String role_name) {
        PageResult<Role> result = roleService.listRoles(page, pageSize, role_name);
        return Result.success(result);
    }

    /**
     * 添加角色
     */
    @PostMapping
    public Result<?> addRole(@RequestBody Role role) {
        roleService.addRole(role);
        return Result.success();
    }

    /**
     * 根据ID查询角色
     */
    @GetMapping("/{id}")
    public Result<Role> getRoleById(@PathVariable Integer id) {
        Role role = roleService.getRoleById(id);
        return Result.success(role);
    }

    /**
     * 修改角色
     */
    @PutMapping
    public Result<?> updateRole(@RequestBody Role role) {
        roleService.updateRole(role);
        return Result.success();
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteRole(@PathVariable Integer id) {
        roleService.deleteRole(id);
        return Result.success();
    }

    /**
     * 查询所有角色（下拉列表）
     */
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> listAllRoles() {
        List<Map<String, Object>> roles = roleService.listAllRoles();
        return Result.success(roles);
    }
}

