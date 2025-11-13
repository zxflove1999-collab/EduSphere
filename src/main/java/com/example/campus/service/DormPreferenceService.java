package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.dto.DormPreferenceRequest;
import com.example.campus.entity.DormPreference;
import com.example.campus.mapper.DormPreferenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
            throw new BusinessException("请填写完整的作息时间");
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
            throw new BusinessException("时间格式错误，应为HH:mm:ss");
        }
        preference.setStudyHabitPreference(request.getStudyHabitPreference());
        preference.setHygienePreference(request.getHygienePreference());
        preference.setNoiseTolerance(request.getNoiseTolerance());
        preference.setSelfIntroduction(request.getSelfIntroduction());
        preference.setSubmissionTime(LocalDateTime.now());

        // 简化逻辑：根据填写完整度给出匹配分数
        int filledFields = 0;
        if (request.getWakeUpTime() != null) filledFields++;
        if (request.getBedtime() != null) filledFields++;
        if (request.getStudyHabitPreference() != null) filledFields++;
        if (request.getHygienePreference() != null) filledFields++;
        if (request.getNoiseTolerance() != null) filledFields++;
        double score = Math.min(1.0, 0.6 + filledFields * 0.08);
        preference.setMatchingScore(BigDecimal.valueOf(score));

        if (preference.getPreferenceId() == null) {
            dormPreferenceMapper.insert(preference);
        } else {
            dormPreferenceMapper.update(preference);
        }
    }
}

