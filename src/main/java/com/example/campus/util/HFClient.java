package com.example.campus.util;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 这是一个“模拟”的 AI 客户端
 * 专门用于演示，不需要 API Key，不会因为网络问题报错
 */
public class HFClient {

    /**
     * 模拟 AI 分析
     * 根据输入的文字包含的关键词，返回假的分析结果
     */
    public static JSONObject analyze(String text) {
        JSONObject result = new JSONObject();
        JSONArray labels = new JSONArray();
        JSONArray scores = new JSONArray();

        // --- 伪 AI 逻辑：根据关键词猜标签 ---
        if (text == null) text = "";
        
        if (text.contains("吵") || text.contains("闹") || text.contains("玩") || text.contains("游戏")) {
            // 如果提到吵闹、游戏 -> 判定为喜欢热闹/打游戏
            labels.put("gaming").put("noisy").put("messy");
            scores.put(0.95).put(0.85).put(0.5);
        } else if (text.contains("静") || text.contains("书") || text.contains("习") || text.contains("睡")) {
            // 如果提到安静、学习 -> 判定为喜欢安静/学习
            labels.put("quiet").put("study").put("tidy");
            scores.put(0.99).put(0.88).put(0.6);
        } else {
            // 默认返回：喜欢安静
            labels.put("quiet").put("tidy").put("study");
            scores.put(0.8).put(0.5).put(0.3);
        }

        result.put("labels", labels);
        result.put("scores", scores);
        
        // 打印日志，假装正在努力工作
        System.out.println("【AI模拟】已分析文本: " + text);
        System.out.println("【AI模拟】分析结果: " + labels.toString());
        
        return result;
    }
}