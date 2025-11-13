package com.example.campus.controller;

import com.example.campus.common.Result;
import com.example.campus.dto.PasswordChangeRequest;
import com.example.campus.dto.ProfileUpdateRequest;
import com.example.campus.service.ProfileService;
import com.example.campus.util.JwtUtil;
import com.example.campus.vo.UserProfileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 个人中心控制器
 */
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    private final JwtUtil jwtUtil;

    /**
     * 获取当前登录用户信息
     */
    @GetMapping
    public Result<UserProfileVO> getProfile(@RequestHeader("token") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        UserProfileVO profile = profileService.getProfile(userId);
        return Result.success(profile);
    }

    /**
     * 修改当前用户信息
     */
    @PutMapping
    public Result<?> updateProfile(@RequestHeader("token") String token,
                                   @RequestBody ProfileUpdateRequest request) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        profileService.updateProfile(userId, request);
        return Result.success();
    }

    /**
     * 修改当前用户密码
     */
    @PutMapping("/password")
    public Result<?> changePassword(@RequestHeader("token") String token,
                                    @RequestBody PasswordChangeRequest request) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        profileService.changePassword(userId, request);
        return Result.success();
    }
}

