package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.common.PageResult;
import com.example.campus.entity.Role;
import com.example.campus.entity.TeacherProfile;
import com.example.campus.entity.User;
import com.example.campus.entity.UserRole;
import com.example.campus.mapper.RoleMapper;
import com.example.campus.mapper.TeacherProfileMapper;
import com.example.campus.mapper.UserMapper;
import com.example.campus.mapper.UserRoleMapper;
import com.example.campus.util.PasswordUtil;
import com.example.campus.vo.TeacherVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 教师管理服务
 */
@Service
@RequiredArgsConstructor
public class TeacherService {
    private final UserMapper userMapper;
    private final TeacherProfileMapper teacherProfileMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final PasswordUtil passwordUtil;

    /**
     * 教师列表查询
     */
    public PageResult<TeacherVO> listTeachers(Integer page, Integer pageSize, String fullName,
                                               String teacherIdNumber, String department, String title, Integer status) {
        PageHelper.startPage(page != null ? page : 1, pageSize != null ? pageSize : 10);
        List<User> users = userMapper.selectTeachersByCondition(fullName, teacherIdNumber, department, title, status);
        PageInfo<User> pageInfo = new PageInfo<>(users);
        
        List<TeacherVO> teacherVOs = users.stream().map(user -> {
            TeacherVO vo = new TeacherVO();
            BeanUtils.copyProperties(user, vo);
            TeacherProfile profile = teacherProfileMapper.selectByUserId(user.getUserId());
            if (profile != null) {
                vo.setTeacherIdNumber(profile.getTeacherIdNumber());
                vo.setGender(profile.getGender());
                vo.setDepartment(profile.getDepartment());
                vo.setTitle(profile.getTitle());
                vo.setOfficeLocation(profile.getOfficeLocation());
            }
            return vo;
        }).collect(Collectors.toList());
        
        return new PageResult<>(pageInfo.getTotal(), teacherVOs);
    }

    /**
     * 添加教师
     */
    @Transactional
    public void addTeacher(User user, TeacherProfile profile) {
        // 检查用户名是否已存在
        User existing = userMapper.selectByUsername(user.getUsername());
        if (existing != null) {
            throw new BusinessException("用户名已存在");
        }

        // 加密密码
        if (user.getPasswordHash() == null || user.getPasswordHash().isEmpty()) {
            user.setPasswordHash(passwordUtil.encode("123456"));
        } else {
            user.setPasswordHash(passwordUtil.encode(user.getPasswordHash()));
        }

        user.setStatus(1);
        user.setTokenVersion(1);
        userMapper.insert(user);

        // 创建教师资料
        profile.setUserId(user.getUserId());
        profile.setTeacherIdNumber(user.getUsername()); // 教工号等于用户名
        teacherProfileMapper.insert(profile);

        // 分配教师角色
        Role teacherRole = roleMapper.selectByRoleKey("teacher");
        if (teacherRole != null) {
            UserRole userRole = new UserRole();
            userRole.setUserId(user.getUserId());
            userRole.setRoleId(teacherRole.getRoleId());
            userRoleMapper.insert(userRole);
        }
    }

    /**
     * 根据ID查询教师
     */
    public TeacherVO getTeacherById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("教师不存在");
        }

        TeacherVO vo = new TeacherVO();
        BeanUtils.copyProperties(user, vo);
        TeacherProfile profile = teacherProfileMapper.selectByUserId(userId);
        if (profile != null) {
            vo.setTeacherIdNumber(profile.getTeacherIdNumber());
            vo.setGender(profile.getGender());
            vo.setDepartment(profile.getDepartment());
            vo.setTitle(profile.getTitle());
            vo.setOfficeLocation(profile.getOfficeLocation());
        }
        return vo;
    }

    /**
     * 修改教师资料
     */
    @Transactional
    public void updateTeacher(User user, TeacherProfile profile) {
        User existing = userMapper.selectById(user.getUserId());
        if (existing == null) {
            throw new BusinessException("教师不存在");
        }

        userMapper.update(user);
        if (profile != null) {
            profile.setUserId(user.getUserId());
            teacherProfileMapper.update(profile);
        }
    }

    /**
     * 删除教师
     */
    @Transactional
    public void deleteTeachers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new BusinessException("教师ID不能为空");
        }
        List<User> existingUsers = userMapper.selectByIds(userIds);
        Set<Long> existingIds = existingUsers.stream()
                .map(User::getUserId)
                .collect(Collectors.toSet());
        List<Long> missing = userIds.stream()
                .filter(id -> !existingIds.contains(id))
                .collect(Collectors.toList());
        if (!missing.isEmpty()) {
            throw new BusinessException("部分教师不存在");
        }
        for (Long userId : userIds) {
            try {
                userMapper.deleteById(userId);
            } catch (DataIntegrityViolationException ex) {
                throw new BusinessException("该教师存在关联数据，无法删除");
            }
        }
    }
}

