package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.dto.DormPreferenceRequest;
import com.example.campus.entity.DormPreference;
import com.example.campus.mapper.DormPreferenceMapper;
import com.example.campus.util.HFClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

// ★ 必须的 import（你缺少这些导致编译错误）
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * 宿舍偏好问卷服务
 */
@Service
@RequiredArgsConstructor
public class DormPreferenceService {

    private final DormPreferenceMapper dormPreferenceMapper;

    public DormPreference getPreferenceByStudent(Long studentId) {
        return dormPreferenceMapper.selectByStudentId(studentId);
    }

    @Transactional
    public void submitPreference(Long studentId, DormPreferenceRequest request) {
        if (request.getWakeUpTime() == null || request.getBedtime() == null) {
            throw new BusinessException("Please provide wake up time and bedtime.");
        }

        DormPreference preference = dormPreferenceMapper.selectByStudentId(studentId);
        if (preference == null) {
            preference = new DormPreference();
            preference.setStudentId(studentId);
        }

        try {
            preference.setWakeUpTime(LocalTime.parse(request.getWakeUpTime()));
            preference.setBedtime(LocalTime.parse(request.getBedtime()));
        } catch (Exception ex) {
            throw new BusinessException("Invalid time format. Use HH:mm:ss");
        }

        preference.setStudyHabitPreference(request.getStudyHabitPreference());
        preference.setHygienePreference(request.getHygienePreference());
        preference.setNoiseTolerance(request.getNoiseTolerance());
        preference.setSelfIntroduction(request.getSelfIntroduction());
        preference.setSubmissionTime(LocalDateTime.now());

        // ===========================
        // AI 分析 extra text using HuggingFace
        // ===========================
        String note = request.getSelfIntroduction();
        if (note != null && note.trim().length() > 3) {
            try {
                JSONObject result = HFClient.analyze(note);

                JSONArray labels = result.getJSONArray("labels");
                JSONArray scores = result.getJSONArray("scores");

                for (int i = 0; i < labels.length(); i++) {
                    String label = labels.getString(i);
                    double score = scores.getDouble(i);

                    if (label.equals("quiet") && score > 0.5) {
                        preference.setNoiseTolerance(0);
                    }
                    if (label.equals("noisy") && score > 0.5) {
                        preference.setNoiseTolerance(2);
                    }
                    if (label.equals("tidy") && score > 0.5) {
                        preference.setHygienePreference(1);
                    }
                    if (label.equals("messy") && score > 0.5) {
                        preference.setHygienePreference(0);
                    }
                    if (label.equals("study") && score > 0.5) {
                        preference.setStudyHabitPreference(1);
                    }
                    if (label.equals("gaming") && score > 0.5) {
                        preference.setStudyHabitPreference(0);
                    }
                }
            } catch (Exception e) {
                // AI 失败不影响主流程
                System.out.println("AI analysis failed: " + e.getMessage());
            }
        }

        // 简单评分机制
        int filled = 0;
        if (request.getWakeUpTime() != null) filled++;
        if (request.getBedtime() != null) filled++;
        if (request.getStudyHabitPreference() != null) filled++;
        if (request.getHygienePreference() != null) filled++;
        if (request.getNoiseTolerance() != null) filled++;

        double score = Math.min(1.0, 0.6 + filled * 0.08);
        preference.setMatchingScore(BigDecimal.valueOf(score));

        if (preference.getPreferenceId() == null) {
            dormPreferenceMapper.insert(preference);
        } else {
            dormPreferenceMapper.update(preference);
        }
    }
}
