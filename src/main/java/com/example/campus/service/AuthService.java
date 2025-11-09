package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.dto.LoginRequest;
import com.example.campus.dto.LoginResponse;
import com.example.campus.entity.User;
import com.example.campus.mapper.UserMapper;
import com.example.campus.mapper.UserRoleMapper;
import com.example.campus.util.JwtUtil;
import com.example.campus.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 认证服务
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final JwtUtil jwtUtil;
    private final PasswordUtil passwordUtil;

    /**
     * 用户登录
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 查询用户
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 验证密码
        if (!passwordUtil.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查账号状态
        if (user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        // 获取用户角色
        List<String> roleKeys = userRoleMapper.selectRoleKeysByUserId(user.getUserId());

        // 生成JWT令牌
        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), roleKeys);

        // 更新最后登录时间
        user.setLastLoginAt(LocalDateTime.now());
        userMapper.update(user);

        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        response.setToken(token);
        response.setRoles(roleKeys);

        return response;
    }
}

