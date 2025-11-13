package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.common.PageResult;
import com.example.campus.entity.Role;
import com.example.campus.mapper.RoleMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 角色管理服务
 */
@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleMapper roleMapper;

    /**
     * 角色列表查询
     */
    public PageResult<Role> listRoles(Integer page, Integer pageSize, String roleName) {
        PageHelper.startPage(page != null ? page : 1, pageSize != null ? pageSize : 10);
        List<Role> roles = roleMapper.selectByCondition(roleName);
        PageInfo<Role> pageInfo = new PageInfo<>(roles);
        return new PageResult<>(pageInfo.getTotal(), roles);
    }

    /**
     * 添加角色
     */
    @Transactional
    public void addRole(Role role) {
        // 检查role_key是否已存在
        Role existing = roleMapper.selectByRoleKey(role.getRoleKey());
        if (existing != null) {
            throw new BusinessException("角色标识已存在");
        }
        role.setIsSystem(0);
        roleMapper.insert(role);
    }

    /**
     * 根据ID查询角色
     */
    public Role getRoleById(Integer roleId) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        return role;
    }

    /**
     * 修改角色
     */
    @Transactional
    public void updateRole(Role role) {
        Role existing = roleMapper.selectById(role.getRoleId());
        if (existing == null) {
            throw new BusinessException("角色不存在");
        }
        // 检查role_key是否与其他角色冲突
        Role byKey = roleMapper.selectByRoleKey(role.getRoleKey());
        if (byKey != null && !byKey.getRoleId().equals(role.getRoleId())) {
            throw new BusinessException("角色标识已存在");
        }
        roleMapper.update(role);
    }

    /**
     * 删除角色
     */
    @Transactional
    public void deleteRole(Integer roleId) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        if (role.getIsSystem() == 1) {
            throw new BusinessException("系统角色不能删除");
        }
        roleMapper.deleteById(roleId);
    }

    /**
     * 查询所有角色（下拉列表）
     */
    public List<Map<String, Object>> listAllRoles() {
        List<Map<String, Object>> result = roleMapper.selectSimpleList();
        return result;
    }
}

