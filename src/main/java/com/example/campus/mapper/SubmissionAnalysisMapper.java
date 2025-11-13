package com.example.campus.mapper;

import com.example.campus.entity.SubmissionAnalysis;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * AI作业分析Mapper接口
 */
@Mapper
public interface SubmissionAnalysisMapper {
    SubmissionAnalysis selectBySubmissionId(@Param("submissionId") Long submissionId);
    int insert(SubmissionAnalysis analysis);
    int update(SubmissionAnalysis analysis);
}

