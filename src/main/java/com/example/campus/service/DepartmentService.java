package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.common.PageResult;
import com.example.campus.entity.Department;
import com.example.campus.mapper.DepartmentMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 院系管理服务
 */
@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentMapper departmentMapper;

    /**
     * 院系列表查询
     */
    public PageResult<Department> listDepartments(Integer page, Integer pageSize, String departmentName) {
        PageHelper.startPage(page != null ? page : 1, pageSize != null ? pageSize : 10);
        List<Department> departments = departmentMapper.selectByCondition(departmentName);
        PageInfo<Department> pageInfo = new PageInfo<>(departments);
        return new PageResult<>(pageInfo.getTotal(), departments);
    }

    /**
     * 添加院系
     */
    @Transactional
    public void addDepartment(Department department) {
        departmentMapper.insert(department);
    }

    /**
     * 根据ID查询院系
     */
    public Department getDepartmentById(Integer departmentId) {
        Department department = departmentMapper.selectById(departmentId);
        if (department == null) {
            throw new BusinessException("院系不存在");
        }
        return department;
    }

    /**
     * 修改院系
     */
    @Transactional
    public void updateDepartment(Department department) {
        Department existing = departmentMapper.selectById(department.getDepartmentId());
        if (existing == null) {
            throw new BusinessException("院系不存在");
        }
        departmentMapper.update(department);
    }

    /**
     * 删除院系
     */
    @Transactional
    public void deleteDepartment(Integer departmentId) {
        Department existing = departmentMapper.selectById(departmentId);
        if (existing == null) {
            throw new BusinessException("院系不存在");
        }
        departmentMapper.deleteById(departmentId);
    }

    /**
     * 查询所有院系 (下拉列表)
     */
    public List<Map<String, Object>> listAllDepartments() {
        return departmentMapper.selectSimpleList();
    }
}

