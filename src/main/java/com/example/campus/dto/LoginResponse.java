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


    // 添加无参构造方法（Lombok会自动生成，但明确写出更清晰）
    public LoginResponse() {}

    // 添加有参构造方法，方便创建对象
    public LoginResponse(Long userId, String username, String fullName, String token, List<String> roles) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.token = token;
        this.roles = roles;
    }
}

