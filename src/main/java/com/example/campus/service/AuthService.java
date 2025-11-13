package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.dto.LoginRequest;
import com.example.campus.dto.LoginResponse;
import com.example.campus.entity.User;
import com.example.campus.mapper.UserMapper;
import com.example.campus.mapper.UserRoleMapper;
import com.example.campus.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 认证服务
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final JwtUtil jwtUtil;

    private final AuthenticationManager authenticationManager; // 添加这个

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9]+$");

    /**
     * 用户登录
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        if (request == null || !StringUtils.hasText(request.getUsername())) {
            throw new BusinessException("username为必填项");
        }
        String username = request.getUsername().trim();
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new BusinessException("用户名格式错误（应为学号/工号）");
        }
        if (!StringUtils.hasText(request.getPassword())) {
            throw new BusinessException("password为必填项");
        }
        request.setUsername(username);

        try {
            // 使用Spring Security进行认证（这会验证密码）
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new BusinessException("用户名或密码错误");
        }

        // 查询用户（认证成功后再查询详细信息）
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException("用户不存在");

        }

        // 检查账号状态
        if (user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用，请联系管理员");
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

