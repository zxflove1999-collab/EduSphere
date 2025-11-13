package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.dto.PasswordChangeRequest;
import com.example.campus.dto.ProfileUpdateRequest;
import com.example.campus.entity.StudentProfile;
import com.example.campus.entity.TeacherProfile;
import com.example.campus.entity.User;
import com.example.campus.mapper.RoleMapper;
import com.example.campus.mapper.StudentProfileMapper;
import com.example.campus.mapper.TeacherProfileMapper;
import com.example.campus.mapper.UserMapper;
import com.example.campus.mapper.UserRoleMapper;
import com.example.campus.util.PasswordUtil;
import com.example.campus.vo.RoleVO;
import com.example.campus.vo.UserProfileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 个人中心服务
 */
@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final TeacherProfileMapper teacherProfileMapper;
    private final PasswordUtil passwordUtil;

    /**
     * 获取当前登录用户信息
     */
    public UserProfileVO getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserProfileVO vo = new UserProfileVO();
        BeanUtils.copyProperties(user, vo);

        // 获取角色信息
        List<Integer> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
        List<RoleVO> roles = roleIds.stream().map(roleId -> {
            RoleVO roleVO = new RoleVO();
            // 查询角色详情
            com.example.campus.entity.Role role = roleMapper.selectById(roleId);
            if (role != null) {
                roleVO.setRoleId(role.getRoleId());
                roleVO.setRoleName(role.getRoleName());
                roleVO.setRoleKey(role.getRoleKey());
                roleVO.setDescription(role.getDescription());
                roleVO.setIsSystem(role.getIsSystem());
                if (role.getCreatedAt() != null) {
                    roleVO.setCreatedAt(role.getCreatedAt().toString());
                }
            }
            return roleVO;
        }).collect(Collectors.toList());
        vo.setRoles(roles);

        // 获取详细资料
        Map<String, Object> profile = new HashMap<>();
        StudentProfile studentProfile = studentProfileMapper.selectByUserId(userId);
        if (studentProfile != null) {
            profile.put("student_id_number", studentProfile.getStudentIdNumber());
            profile.put("gender", studentProfile.getGender());
            profile.put("major", studentProfile.getMajor());
            profile.put("class_name", studentProfile.getClassName());
            profile.put("grade", studentProfile.getGrade());
            profile.put("enrollment_date", studentProfile.getEnrollmentDate());
        } else {
            TeacherProfile teacherProfile = teacherProfileMapper.selectByUserId(userId);
            if (teacherProfile != null) {
                profile.put("teacher_id_number", teacherProfile.getTeacherIdNumber());
                profile.put("gender", teacherProfile.getGender());
                profile.put("department", teacherProfile.getDepartment());
                profile.put("title", teacherProfile.getTitle());
                profile.put("office_location", teacherProfile.getOfficeLocation());
            }
        }
        vo.setProfile(profile);

        return vo;
    }

    /**
     * 修改当前用户信息
     */
    @Transactional
    public void updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 更新基本信息
        if (request.getBasicInfo() != null) {
            Map<String, Object> basicInfo = request.getBasicInfo();
            if (basicInfo.containsKey("email")) {
                user.setEmail((String) basicInfo.get("email"));
            }
            if (basicInfo.containsKey("phone_number")) {
                user.setPhoneNumber((String) basicInfo.get("phone_number"));
            }
            userMapper.update(user);
        }

        // 更新详细资料
        if (request.getProfileInfo() != null) {
            StudentProfile studentProfile = studentProfileMapper.selectByUserId(userId);
            if (studentProfile != null) {
                Map<String, Object> profileInfo = request.getProfileInfo();
                if (profileInfo.containsKey("major")) {
                    studentProfile.setMajor((String) profileInfo.get("major"));
                }
                if (profileInfo.containsKey("class_name")) {
                    studentProfile.setClassName((String) profileInfo.get("class_name"));
                }
                studentProfileMapper.update(studentProfile);
            } else {
                TeacherProfile teacherProfile = teacherProfileMapper.selectByUserId(userId);
                if (teacherProfile != null) {
                    Map<String, Object> profileInfo = request.getProfileInfo();
                    if (profileInfo.containsKey("office_location")) {
                        teacherProfile.setOfficeLocation((String) profileInfo.get("office_location"));
                    }
                    if (profileInfo.containsKey("research_area")) {
                        teacherProfile.setResearchArea((String) profileInfo.get("research_area"));
                    }
                    teacherProfileMapper.update(teacherProfile);
                }
            }
        }
    }

    /**
     * 修改当前用户密码
     */
    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码
        if (!passwordUtil.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new BusinessException("旧密码错误");
        }

        // 更新密码
        String newPasswordHash = passwordUtil.encode(request.getNewPassword());
        userMapper.updatePassword(userId, newPasswordHash);
    }
}

