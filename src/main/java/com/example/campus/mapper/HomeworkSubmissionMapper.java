package com.example.campus.mapper;

import com.example.campus.entity.HomeworkSubmission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 作业提交Mapper接口
 */
@Mapper
public interface HomeworkSubmissionMapper {
    HomeworkSubmission selectById(@Param("submissionId") Long submissionId);
    HomeworkSubmission selectByHomeworkAndStudent(@Param("homeworkId") Long homeworkId, @Param("studentId") Long studentId);
    List<HomeworkSubmission> selectByHomeworkId(@Param("homeworkId") Long homeworkId, @Param("status") Integer status);
    List<HomeworkSubmission> selectByStudentId(@Param("studentId") Long studentId);
    int insert(HomeworkSubmission submission);
    int update(HomeworkSubmission submission);
    int deleteById(@Param("submissionId") Long submissionId);
}

