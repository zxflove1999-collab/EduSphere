package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.entity.DormSurvey;
import com.example.campus.mapper.DormSurveyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    public void submitSurvey(DormSurvey survey) {
        if (survey.getSubmissionTime() == null) {
            survey.setSubmissionTime(LocalDateTime.now());
        }
        DormSurvey existing = dormSurveyMapper.selectByStudentId(survey.getStudentId());
        if (existing != null) {
            survey.setSurveyId(existing.getSurveyId());
            dormSurveyMapper.update(survey);
        } else {
            dormSurveyMapper.insert(survey);
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
}

