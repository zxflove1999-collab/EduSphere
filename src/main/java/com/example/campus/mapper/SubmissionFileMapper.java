package com.example.campus.mapper;

import com.example.campus.entity.SubmissionFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 作业附件Mapper接口
 */
@Mapper
public interface SubmissionFileMapper {
    List<SubmissionFile> selectBySubmissionId(@Param("submissionId") Long submissionId);
    int insert(SubmissionFile file);
    int deleteById(@Param("fileId") Long fileId);
    int deleteBySubmissionId(@Param("submissionId") Long submissionId);
}

