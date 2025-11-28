package com.example.campus.service;

import com.example.campus.dto.DormPreferenceRequest;
import com.example.campus.entity.DormQuestionnaire;
import com.example.campus.mapper.DormQuestionnaireMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DormQuestionnaireService {

    private final DormQuestionnaireMapper questionnaireMapper;
    private final DormPreferenceService preferenceService;  // 调用偏好服务

    /**
     * 处理学生宿舍问卷提交
     */
    public void submitQuestionnaire(DormQuestionnaire q) {

        // 1. 保存18道题的原始问卷
        questionnaireMapper.insert(q);

        // 2. 转换问卷→偏好结构
        DormPreferenceRequest req = buildPreferenceRequest(q);

        // 3. 调用写好的接口保存偏好
        preferenceService.submitPreference(q.getStudentId(), req);
    }

    /**
     * 将18题问卷转换成宿舍偏好
     */
    private DormPreferenceRequest buildPreferenceRequest(DormQuestionnaire q) {
        DormPreferenceRequest dto = new DormPreferenceRequest();

        // 作息映射
        dto.setBedtime(q.getQ1SleepTime() == 0 ? "22:00:00" : "00:00:00");
        dto.setWakeUpTime(q.getQ2WakeTime() == 0 ? "07:00:00" : "09:00:00");

        // 学习偏好（直接使用第14题）
        dto.setStudyHabitPreference(q.getQ14QuietStudy());

        // 卫生偏好（使用第11题）
        dto.setHygienePreference(q.getQ11CleanDemand());

        // 噪音容忍度（使用第15题）
        dto.setNoiseTolerance(q.getQ15NoiseTolerance());

        // 自我介绍来自开放题 extraNote
        dto.setSelfIntroduction(q.getExtraNote());

        return dto;
    }
}
