package com.example.campus.dto;

import lombok.Data;
import java.util.Map;

/**
 * 更新个人资料请求DTO
 */
@Data
public class ProfileUpdateRequest {
    private Map<String, Object> basicInfo;
    private Map<String, Object> profileInfo;
}

