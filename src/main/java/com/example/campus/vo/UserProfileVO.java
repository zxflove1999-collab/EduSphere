package com.example.campus.vo;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 用户资料VO
 */
@Data
public class UserProfileVO {
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private List<RoleVO> roles;
    private Map<String, Object> profile;
}

