package com.example.campus.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.campus.common.BusinessException;
import com.example.campus.dto.DormPreferenceRequest;
import com.example.campus.dto.DormSurveyRequest;
import com.example.campus.entity.DormSurvey;
import com.example.campus.mapper.DormSurveyMapper;

import lombok.RequiredArgsConstructor;
/**
 * 宿舍问卷服务
 */

@Service
@RequiredArgsConstructor

public class DormSurveyService {

    private final DormSurveyMapper dormSurveyMapper;
    // 注入偏好服务，用于调用AI
    private final DormPreferenceService dormPreferenceService;
    // 注入自动分配服务，用于提交后立即触发分配
    private final AutoDormAllocationService autoDormAllocationService;

    public DormSurvey getSurveyByStudent(Long studentId) {
        return dormSurveyMapper.selectByStudentId(studentId);
    }

    @Transactional
    public void submitSurvey(Long studentId, DormSurveyRequest request) {
        // --- 1. 保存详细问卷数据 (存留底) ---
        DormSurvey survey = dormSurveyMapper.selectByStudentId(studentId);
        if (survey == null) {
            survey = new DormSurvey();
            survey.setStudentId(studentId);
        }

        // 时间处理：防止空指针或格式错误
        try {
            if (StringUtils.hasText(request.getWakeUpTime())) {
                survey.setWakeUpTime(parseTime(request.getWakeUpTime()));
            }
            if (StringUtils.hasText(request.getSleepTime())) {
                survey.setSleepTime(parseTime(request.getSleepTime()));
            }
        } catch (Exception e) {
            // 忽略时间格式错误，保持原值或null
        }

        // 设置详细字段
        survey.setCleanlinessPreference(request.getCleanlinessPreference());
        survey.setNoiseTolerance(request.getNoiseTolerance());
        survey.setSleepTimePref(request.getSleepTimePref());
        survey.setWakeTimePref(request.getWakeTimePref());
        survey.setNapHabit(request.getNapHabit());
        survey.setSmokingStatus(request.getSmokingStatus());
        survey.setRoommateSmokeAccept(request.getRoommateSmokeAccept());
        survey.setGamingFreq(request.getGamingFreq());
        survey.setHeadphoneUsage(request.getHeadphoneUsage());
        survey.setChattingPref(request.getChattingPref());
        survey.setGuestAcceptance(request.getGuestAcceptance());
        survey.setGroupActivityWillingness(request.getGroupActivityWillingness());
        survey.setHygieneRequirement(request.getHygieneRequirement());
        survey.setOrganizationLevel(request.getOrganizationLevel());
        survey.setRoommateHygieneTolerance(request.getRoommateHygieneTolerance());
        survey.setQuietStudyNeed(request.getQuietStudyNeed());
        survey.setNoiseToleranceLevel(request.getNoiseToleranceLevel());
        survey.setDormFoodFreq(request.getDormFoodFreq());
        survey.setFragranceUsage(request.getFragranceUsage());
        survey.setSmellSensitivity(request.getSmellSensitivity());
        survey.setSubmissionTime(LocalDateTime.now());

        // 保存或更新问卷记录
        if (survey.getSurveyId() == null) {
            dormSurveyMapper.insert(survey);
        } else {
            dormSurveyMapper.update(survey);
        }

        // --- 2. 转换数据并调用偏好服务 (此处会触发 AI 分析) ---
        DormPreferenceRequest prefRequest = new DormPreferenceRequest();
        
        // 映射必填字段，给默认值防崩
        prefRequest.setWakeUpTime(request.getWakeUpTime() != null ? request.getWakeUpTime() : "07:00:00");
        prefRequest.setBedtime(request.getSleepTime() != null ? request.getSleepTime() : "23:00:00");
        
        // 映射关键指标供分配算法使用
        prefRequest.setStudyHabitPreference(request.getQuietStudyNeed()); // 1:需要安静, 2:不需要
        prefRequest.setHygienePreference(request.getHygieneRequirement()); // 卫生要求
        prefRequest.setNoiseTolerance(request.getNoiseTolerance()); // 噪音容忍度
        
        // ★ 将前端传来的特殊需求传入，用于触发 AI 分析
        prefRequest.setSelfIntroduction(request.getSelfIntroduction());

        // 提交偏好 (DormPreferenceService 内部会调用 HFClient.analyze)
        dormPreferenceService.submitPreference(studentId, prefRequest);

        // --- 3. 立即触发自动分配算法 (演示效果关键) ---
        // try {
        //     System.out.println(">>> 问卷提交成功，开始执行自动分配...");
        //     autoDormAllocationService.runAllocation();
        //     System.out.println(">>> 自动分配执行完毕！");
        // } catch (Exception e) {
        //     e.printStackTrace();
        //     System.out.println("自动分配过程出现异常，但不影响问卷提交");
        // }
    }

    @Transactional
    public void calculateMatchingScore(Long surveyId) {
        DormSurvey survey = dormSurveyMapper.selectById(surveyId);
        if (survey == null) {
            throw new BusinessException("问卷不存在");
        }
        // 简化的匹配算法示例
        BigDecimal score = BigDecimal.valueOf(80.0);
        survey.setMatchingScore(score);
        dormSurveyMapper.update(survey);
    }

    public List<DormSurvey> getSurveysByMatchingScore(BigDecimal minScore) {
        return dormSurveyMapper.selectByMatchingScore(minScore);
    }

    private LocalTime parseTime(String text) {
        try {
            // 支持简单的时间格式修正，例如 7:00 -> 07:00
            if (text.length() == 4 && text.indexOf(':') == 1) {
                text = "0" + text;
            }
            // 如果只有 HH:mm，补上 :00
            if (text.length() == 5) {
                text = text + ":00";
            }
            return LocalTime.parse(text);
        } catch (Exception ex) {
            // 如果解析失败，返回默认值，防止报错中断流程
            return LocalTime.of(0, 0);
        }
    }
}