package com.example.campus.dto;

import lombok.Data;
import java.util.List;

/**
 * 登录响应DTO
 */
@Data
public class LoginResponse {
    private Long userId;
    private String username;
    private String fullName;
    private String token;
    private List<String> roles;
}

