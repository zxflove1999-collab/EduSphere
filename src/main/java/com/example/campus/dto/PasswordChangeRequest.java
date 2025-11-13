package com.example.campus.dto;

import lombok.Data;

/**
 * 修改密码请求DTO
 */
@Data
public class PasswordChangeRequest {
    private String oldPassword;
    private String newPassword;
}

