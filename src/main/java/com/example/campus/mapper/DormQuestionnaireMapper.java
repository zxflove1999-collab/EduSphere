package com.example.campus.mapper;

import com.example.campus.entity.DormQuestionnaire;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DormQuestionnaireMapper {

    // 插入一条问卷记录
    int insert(DormQuestionnaire questionnaire);

    // 根据 studentId 查询最近一次问卷
    DormQuestionnaire selectByStudentId(Long studentId);
}
