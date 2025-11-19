package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.dto.DormSurveyRequest;
import com.example.campus.entity.DormSurvey;
import com.example.campus.mapper.DormSurveyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 宿舍问卷服务
 */
@Service
@RequiredArgsConstructor
public class DormSurveyService {
    private final DormSurveyMapper dormSurveyMapper;

    public DormSurvey getSurveyByStudent(Long studentId) {
        return dormSurveyMapper.selectByStudentId(studentId);
    }

    @Transactional
    public void submitSurvey(Long studentId, DormSurveyRequest request) {
        DormSurvey survey = dormSurveyMapper.selectByStudentId(studentId);
        if (survey == null) {
            survey = new DormSurvey();
            survey.setStudentId(studentId);
        }

        if (StringUtils.hasText(request.getWakeUpTime())) {
            survey.setWakeUpTime(parseTime(request.getWakeUpTime()));
        }
        if (StringUtils.hasText(request.getSleepTime())) {
            survey.setSleepTime(parseTime(request.getSleepTime()));
        }
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

        if (survey.getSurveyId() == null) {
            dormSurveyMapper.insert(survey);
        } else {
            dormSurveyMapper.update(survey);
        }
    }

    @Transactional
    public void calculateMatchingScore(Long surveyId) {
        DormSurvey survey = dormSurveyMapper.selectById(surveyId);
        if (survey == null) {
            throw new BusinessException("问卷不存在");
        }
        // 简化的匹配算法，实际应该根据多个学生的问卷进行匹配
        BigDecimal score = BigDecimal.valueOf(80.0); // 示例分数
        survey.setMatchingScore(score);
        dormSurveyMapper.update(survey);
    }

    public List<DormSurvey> getSurveysByMatchingScore(BigDecimal minScore) {
        return dormSurveyMapper.selectByMatchingScore(minScore);
    }

    private LocalTime parseTime(String text) {
        try {
            return LocalTime.parse(text);
        } catch (Exception ex) {
            throw new BusinessException("时间格式错误，应为HH:mm:ss");
        }
    }
}

