package com.example.campus.util;

import com.example.campus.common.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class PythonAIServiceClient {
    private static final String AI_SERVICE_URL = "http://localhost:8000/api";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public PythonAIServiceClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 调用 AI 服务进行学生作业分析。期望返回 Map (JSON Object)。
     */
    public Map<String, Object> analyzeStudentSubmission(Map<String, Object> requestData) {
        return callAIServiceForMap("/analyze/student", requestData);
    }

    /**
     * 调用 AI 服务生成教学建议。期望返回 List (JSON Array)。
     * 【已修复】方法签名改为返回 List<Map<String, Object>>，与实际数据结构一致。
     */
    public List<Map<String, Object>> generateTeachingSuggestions(Map<String, Object> requestData) {
        return callAIServiceForList("/analyze/teaching", requestData);
    }

    /**
     * 私有方法：调用 AI 服务，并期望返回 Map 类型的 data 字段内容。
     */
    private Map<String, Object> callAIServiceForMap(String endpoint, Map<String, Object> requestData) {
        Object data = callAIService(endpoint, requestData);
        if (data instanceof Map) {
            return (Map<String, Object>) data;
        }
        // 如果返回的 data 不是 Map，说明 AI 服务返回了意料之外的结构
        throw new BusinessException("AI服务返回数据结构错误，期望JSON对象，但实际是: " + (data != null ? data.getClass().getSimpleName() : "null"));
    }

    /**
     * 私有方法：调用 AI 服务，并期望返回 List 类型的 data 字段内容。
     * 【新增】用于处理 AI 返回 JSON 数组的场景 (例如教学建议)。
     */
    private List<Map<String, Object>> callAIServiceForList(String endpoint, Map<String, Object> requestData) {
        Object data = callAIService(endpoint, requestData);
        if (data instanceof List) {
            // 返回列表
            return (List<Map<String, Object>>) data;
        }
        // 如果返回的 data 不是 List，则抛出错误
        throw new BusinessException("AI服务返回数据结构错误，期望JSON数组，但实际是: " + (data != null ? data.getClass().getSimpleName() : "null"));
    }

    /**
     * 核心私有方法：处理 HTTP 调用和通用响应解析。
     * 返回的是原始的 'data' 字段内容，类型可能是 Map 或 List (Object)。
     */
    private Object callAIService(String endpoint, Map<String, Object> requestData) {
        try {
            String jsonBody = objectMapper.writeValueAsString(requestData);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AI_SERVICE_URL + endpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // 通用解析为 Map，以提取 success 和 data 字段
                Map<String, Object> result = objectMapper.readValue(response.body(), Map.class);

                if (Boolean.TRUE.equals(result.get("success"))) {
                    // 成功，返回 data 字段的原始内容 (可能是 Map 或 List)
                    return result.get("data");
                }

                // 成功返回 200，但 success=false，返回错误信息
                throw new BusinessException("AI服务逻辑失败: " + result.getOrDefault("message", "未知错误"));
            }

            // 非 200 状态码
            throw new BusinessException("AI服务HTTP调用失败，状态码: " + response.statusCode() + ", 详情: " + response.body());

        } catch (IOException | InterruptedException e) {
            log.error("调用Python AI服务异常", e);
            throw new BusinessException("AI服务暂时不可用，请稍后重试");
        }
    }
}