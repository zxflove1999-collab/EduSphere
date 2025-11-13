package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.common.PageResult;
import com.example.campus.entity.Role;
import com.example.campus.entity.StudentProfile;
import com.example.campus.entity.User;
import com.example.campus.entity.UserRole;
import com.example.campus.mapper.RoleMapper;
import com.example.campus.mapper.StudentProfileMapper;
import com.example.campus.mapper.UserMapper;
import com.example.campus.mapper.UserRoleMapper;
import com.example.campus.util.PasswordUtil;
import com.example.campus.vo.StudentVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 学生管理服务
 */
@Service
@RequiredArgsConstructor
public class StudentService {
    private final UserMapper userMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final PasswordUtil passwordUtil;

    /**
     * 学生列表查询
     */
    public PageResult<StudentVO> listStudents(Integer page, Integer pageSize, String fullName,
                                               String studentIdNumber, String major, String className, Integer status) {
        PageHelper.startPage(page != null ? page : 1, pageSize != null ? pageSize : 10);
        List<User> users = userMapper.selectStudentsByCondition(fullName, studentIdNumber, major, className, status);
        PageInfo<User> pageInfo = new PageInfo<>(users);
        
        List<StudentVO> studentVOs = users.stream().map(user -> {
            StudentVO vo = new StudentVO();
            BeanUtils.copyProperties(user, vo);
            StudentProfile profile = studentProfileMapper.selectByUserId(user.getUserId());
            if (profile != null) {
                vo.setStudentIdNumber(profile.getStudentIdNumber());
                vo.setGender(profile.getGender());
                vo.setMajor(profile.getMajor());
                vo.setClassName(profile.getClassName());
                vo.setGrade(profile.getGrade());
                vo.setEnrollmentDate(profile.getEnrollmentDate());
            }
            return vo;
        }).collect(Collectors.toList());
        
        return new PageResult<>(pageInfo.getTotal(), studentVOs);
    }

    /**
     * 添加学生
     */
    @Transactional
    public void addStudent(User user, StudentProfile profile) {
        // 检查用户名是否已存在
        User existing = userMapper.selectByUsername(user.getUsername());
        if (existing != null) {
            throw new BusinessException("用户名已存在");
        }

        // 加密密码
        if (user.getPasswordHash() == null || user.getPasswordHash().isEmpty()) {
            // 默认密码为身份证后6位，这里简化处理
            user.setPasswordHash(passwordUtil.encode("123456"));
        } else {
            user.setPasswordHash(passwordUtil.encode(user.getPasswordHash()));
        }

        user.setStatus(1);
        user.setTokenVersion(1);
        userMapper.insert(user);

        // 创建学生资料
        profile.setUserId(user.getUserId());
        profile.setStudentIdNumber(user.getUsername()); // 学号等于用户名
        studentProfileMapper.insert(profile);

        // 分配学生角色
        Role studentRole = roleMapper.selectByRoleKey("student");
        if (studentRole != null) {
            UserRole userRole = new UserRole();
            userRole.setUserId(user.getUserId());
            userRole.setRoleId(studentRole.getRoleId());
            userRoleMapper.insert(userRole);
        }
    }

    /**
     * 根据ID查询学生
     */
    public StudentVO getStudentById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("学生不存在");
        }

        StudentVO vo = new StudentVO();
        BeanUtils.copyProperties(user, vo);
        StudentProfile profile = studentProfileMapper.selectByUserId(userId);
        if (profile != null) {
            vo.setStudentIdNumber(profile.getStudentIdNumber());
            vo.setGender(profile.getGender());
            vo.setMajor(profile.getMajor());
            vo.setClassName(profile.getClassName());
            vo.setGrade(profile.getGrade());
            vo.setEnrollmentDate(profile.getEnrollmentDate());
        }
        return vo;
    }

    /**
     * 修改学生资料
     */
    @Transactional
    public void updateStudent(User user, StudentProfile profile) {
        User existing = userMapper.selectById(user.getUserId());
        if (existing == null) {
            throw new BusinessException("学生不存在");
        }

        userMapper.update(user);
        if (profile != null) {
            profile.setUserId(user.getUserId());
            studentProfileMapper.update(profile);
        }
    }

    /**
     * 删除学生
     */
    @Transactional
    public void deleteStudents(List<Long> userIds) {
        for (Long userId : userIds) {
            userMapper.deleteById(userId);
            // 级联删除会自动处理关联数据
        }
    }
}

