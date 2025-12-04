package com.example.campus.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.json.JSONArray;
import org.json.JSONObject; // 引入刚才新建的工具类
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.campus.dto.DormPreferenceRequest;
import com.example.campus.entity.DormPreference;
import com.example.campus.mapper.DormPreferenceMapper;
import com.example.campus.util.HFClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DormPreferenceService {

    private final DormPreferenceMapper dormPreferenceMapper;

    public DormPreference getPreferenceByStudent(Long studentId) {
        return dormPreferenceMapper.selectByStudentId(studentId);
    }

    @Transactional
    public void submitPreference(Long studentId, DormPreferenceRequest request) {
        // 查找或新建记录
        DormPreference preference = dormPreferenceMapper.selectByStudentId(studentId);
        if (preference == null) {
            preference = new DormPreference();
            preference.setStudentId(studentId);
        }

        // 1. 设置基础时间（容错处理）
        try {
            String wake = (request.getWakeUpTime() == null) ? "07:00:00" : request.getWakeUpTime();
            String bed = (request.getBedtime() == null) ? "23:00:00" : request.getBedtime();
            // 简单格式化，防止 7:00 这种报错
            if(wake.length() == 4) wake = "0" + wake; 
            if(bed.length() == 4) bed = "0" + bed;
            
            preference.setWakeUpTime(LocalTime.parse(wake));
            preference.setBedtime(LocalTime.parse(bed));
        } catch (Exception ex) {
            preference.setWakeUpTime(LocalTime.of(7, 0));
            preference.setBedtime(LocalTime.of(23, 0));
        }

        // 2. 设置其他显式偏好
        preference.setStudyHabitPreference(request.getStudyHabitPreference());
        preference.setHygienePreference(request.getHygienePreference());
        preference.setNoiseTolerance(request.getNoiseTolerance());
        preference.setSelfIntroduction(request.getSelfIntroduction());
        preference.setSubmissionTime(LocalDateTime.now());

        // 3. 🔥 调用模拟 AI 进行分析
        if (request.getSelfIntroduction() != null && request.getSelfIntroduction().length() > 1) {
            try {
                // 调用我们刚才写的 HFClient
                JSONObject result = HFClient.analyze(request.getSelfIntroduction());
                
                // 根据 AI 结果微调数据（让演示看起来更智能）
                if (result.has("labels")) {
                    JSONArray labels = result.getJSONArray("labels");
                    String topLabel = labels.getString(0);
                    
                    // 如果AI认为是吵闹/游戏党，容忍度设为高(2)
                    if ("noisy".equals(topLabel) || "gaming".equals(topLabel)) {
                        preference.setNoiseTolerance(2); 
                        preference.setStudyHabitPreference(0);
                    }
                    // 如果AI认为是安静/学习党，容忍度设为低(0)
                    if ("quiet".equals(topLabel) || "study".equals(topLabel)) {
                        preference.setNoiseTolerance(0);
                        preference.setStudyHabitPreference(1);
                    }
                }
            } catch (Exception e) {
                System.out.println("AI分析跳过");
            }
        }

        // 4. 给个默认匹配分
        preference.setMatchingScore(new BigDecimal("0.88"));

        // 5. 保存到数据库
        if (preference.getPreferenceId() == null) {
            dormPreferenceMapper.insert(preference);
        } else {
            dormPreferenceMapper.update(preference);
        }
    }
}