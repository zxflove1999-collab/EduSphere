package com.example.campus.mapper;

import com.example.campus.entity.DormSurvey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 宿舍问卷Mapper接口
 */
@Mapper
public interface DormSurveyMapper {
    DormSurvey selectById(@Param("surveyId") Long surveyId);
    DormSurvey selectByStudentId(@Param("studentId") Long studentId);
    List<DormSurvey> selectByMatchingScore(@Param("minScore") java.math.BigDecimal minScore);
    int insert(DormSurvey survey);
    int update(DormSurvey survey);
    int deleteById(@Param("surveyId") Long surveyId);
}

