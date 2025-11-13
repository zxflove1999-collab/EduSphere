package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.common.PageResult;
import com.example.campus.entity.Homework;
import com.example.campus.entity.HomeworkSubmission;
import com.example.campus.entity.SubmissionFile;
import com.example.campus.entity.SubmissionAnalysis;
import com.example.campus.entity.TeachingSuggestion;
import com.example.campus.mapper.ClassMapper;
import com.example.campus.mapper.HomeworkMapper;
import com.example.campus.mapper.HomeworkSubmissionMapper;
import com.example.campus.mapper.SubmissionFileMapper;
import com.example.campus.mapper.SubmissionAnalysisMapper;
import com.example.campus.mapper.TeachingSuggestionMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作业管理服务
 */
@Service
@RequiredArgsConstructor
public class HomeworkService {
    private final HomeworkMapper homeworkMapper;
    private final HomeworkSubmissionMapper homeworkSubmissionMapper;
    private final SubmissionFileMapper submissionFileMapper;
    private final SubmissionAnalysisMapper submissionAnalysisMapper;
    private final TeachingSuggestionMapper teachingSuggestionMapper;
    private final ClassMapper classMapper;

    /**
     * 查询班级作业列表 (教师)
     */
    public PageResult<Homework> listHomeworksByClass(Long classId, Integer page, Integer pageSize) {
        PageHelper.startPage(page != null ? page : 1, pageSize != null ? pageSize : 10);
        List<Homework> homeworks = homeworkMapper.selectByClassId(classId);
        PageInfo<Homework> pageInfo = new PageInfo<>(homeworks);
        return new PageResult<>(pageInfo.getTotal(), homeworks);
    }

    /**
     * 发布作业
     */
    @Transactional
    public void publishHomework(Homework homework, Long createdBy) {
        // 检查班级是否存在
        if (classMapper.selectById(homework.getClassId()) == null) {
            throw new BusinessException("班级不存在");
        }
        homework.setCreatedBy(createdBy);
        homeworkMapper.insert(homework);
    }

    /**
     * 查询作业详情
     */
    public Homework getHomeworkById(Long homeworkId) {
        Homework homework = homeworkMapper.selectById(homeworkId);
        if (homework == null) {
            throw new BusinessException("作业不存在");
        }
        return homework;
    }

    /**
     * 修改作业
     */
    @Transactional
    public void updateHomework(Homework homework) {
        Homework existing = homeworkMapper.selectById(homework.getHomeworkId());
        if (existing == null) {
            throw new BusinessException("作业不存在");
        }
        homeworkMapper.update(homework);
    }

    /**
     * 删除作业
     */
    @Transactional
    public void deleteHomework(Long homeworkId) {
        Homework existing = homeworkMapper.selectById(homeworkId);
        if (existing == null) {
            throw new BusinessException("作业不存在");
        }
        // 检查是否有提交
        int count = homeworkMapper.countSubmissions(homeworkId);
        if (count > 0) {
            throw new BusinessException("该作业已有学生提交，无法删除");
        }
        homeworkMapper.deleteById(homeworkId);
    }

    /**
     * 查询作业提交列表 (教师)
     */
    public PageResult<HomeworkSubmission> listSubmissions(Long homeworkId, Integer page, Integer pageSize, Integer status) {
        PageHelper.startPage(page != null ? page : 1, pageSize != null ? pageSize : 10);
        List<HomeworkSubmission> submissions = homeworkSubmissionMapper.selectByHomeworkId(homeworkId, status);
        PageInfo<HomeworkSubmission> pageInfo = new PageInfo<>(submissions);
        return new PageResult<>(pageInfo.getTotal(), submissions);
    }

    /**
     * 批改作业
     */
    @Transactional
    public void gradeSubmission(Long submissionId, java.math.BigDecimal grade, String teacherFeedback) {
        HomeworkSubmission submission = homeworkSubmissionMapper.selectById(submissionId);
        if (submission == null) {
            throw new BusinessException("提交记录不存在");
        }
        submission.setGrade(grade);
        submission.setTeacherFeedback(teacherFeedback);
        homeworkSubmissionMapper.update(submission);
    }

    /**
     * 获取AI教学建议
     */
    public List<TeachingSuggestion> getTeachingSuggestions(Long homeworkId) {
        return teachingSuggestionMapper.selectByHomeworkId(homeworkId);
    }

    /**
     * 查询班级作业列表 (学生)
     */
    public PageResult<Homework> listHomeworksForStudent(Long classId, Long studentId, Integer page, Integer pageSize) {
        PageHelper.startPage(page != null ? page : 1, pageSize != null ? pageSize : 10);
        List<Homework> homeworks = homeworkMapper.selectByClassId(classId);
        
        // 为每个作业添加学生的提交状态
        for (Homework homework : homeworks) {
            HomeworkSubmission submission = homeworkSubmissionMapper.selectByHomeworkAndStudent(homework.getHomeworkId(), studentId);
            if (submission != null) {
                // 这里可以通过扩展Homework实体或使用VO来返回提交状态
                // 暂时简化处理
            }
        }
        
        PageInfo<Homework> pageInfo = new PageInfo<>(homeworks);
        return new PageResult<>(pageInfo.getTotal(), homeworks);
    }

    /**
     * 查询作业详情 (学生)
     */
    public Map<String, Object> getHomeworkDetailForStudent(Long homeworkId, Long studentId) {
        Homework homework = getHomeworkById(homeworkId);
        HomeworkSubmission submission = homeworkSubmissionMapper.selectByHomeworkAndStudent(homeworkId, studentId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("homework", homework);
        
        if (submission != null) {
            // 获取附件
            List<SubmissionFile> files = submissionFileMapper.selectBySubmissionId(submission.getSubmissionId());
            Map<String, Object> submissionData = new HashMap<>();
            submissionData.put("submission_id", submission.getSubmissionId());
            submissionData.put("content", submission.getContent());
            submissionData.put("submitted_at", submission.getSubmittedAt());
            submissionData.put("status", submission.getStatus());
            submissionData.put("grade", submission.getGrade());
            submissionData.put("teacher_feedback", submission.getTeacherFeedback());
            submissionData.put("files", files);
            result.put("my_submission", submissionData);
        } else {
            result.put("my_submission", null);
        }
        
        return result;
    }

    /**
     * 提交作业
     */
    @Transactional
    public Long submitHomework(Long homeworkId, Long studentId, String content, List<String> fileUrls) {
        Homework homework = getHomeworkById(homeworkId);
        
        // 检查是否已提交
        HomeworkSubmission existing = homeworkSubmissionMapper.selectByHomeworkAndStudent(homeworkId, studentId);
        
        // 判断提交状态
        int status = 1; // 准时提交
        if (LocalDateTime.now().isAfter(homework.getDueDate())) {
            if (homework.getAllowLateSubmission() == 1) {
                status = 2; // 迟交
            } else {
                throw new BusinessException("作业已截止，不允许提交");
            }
        }
        
        if (existing != null) {
            // 更新提交
            existing.setContent(content);
            existing.setStatus(status);
            existing.setSubmittedAt(LocalDateTime.now());
            homeworkSubmissionMapper.update(existing);
            
            // 删除旧附件
            submissionFileMapper.deleteBySubmissionId(existing.getSubmissionId());
            
            // 插入新附件
            if (fileUrls != null && !fileUrls.isEmpty()) {
                for (String fileUrl : fileUrls) {
                    SubmissionFile file = new SubmissionFile();
                    file.setSubmissionId(existing.getSubmissionId());
                    file.setFileUrl(fileUrl);
                    file.setFileName(fileUrl.substring(fileUrl.lastIndexOf("/") + 1));
                    submissionFileMapper.insert(file);
                }
            }
            
            return existing.getSubmissionId();
        } else {
            // 新建提交
            HomeworkSubmission submission = new HomeworkSubmission();
            submission.setHomeworkId(homeworkId);
            submission.setStudentId(studentId);
            submission.setContent(content);
            submission.setStatus(status);
            submission.setSubmittedAt(LocalDateTime.now());
            homeworkSubmissionMapper.insert(submission);
            
            // 插入附件
            if (fileUrls != null && !fileUrls.isEmpty()) {
                for (String fileUrl : fileUrls) {
                    SubmissionFile file = new SubmissionFile();
                    file.setSubmissionId(submission.getSubmissionId());
                    file.setFileUrl(fileUrl);
                    file.setFileName(fileUrl.substring(fileUrl.lastIndexOf("/") + 1));
                    submissionFileMapper.insert(file);
                }
            }
            
            return submission.getSubmissionId();
        }
    }

    /**
     * 获取AI学情分析
     */
    public SubmissionAnalysis getSubmissionAnalysis(Long submissionId) {
        SubmissionAnalysis analysis = submissionAnalysisMapper.selectBySubmissionId(submissionId);
        if (analysis == null) {
            throw new BusinessException("分析报告不存在");
        }
        return analysis;
    }
}

