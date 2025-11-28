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
import com.example.campus.util.PythonAIServiceClient;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.fasterxml.jackson.databind.ObjectMapper; // 【已导入】
import com.fasterxml.jackson.core.JsonProcessingException; // 【已导入】
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 【已导入】
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture; // 【已导入】

/**
 * 作业管理服务
 */
@Service
@RequiredArgsConstructor
@Slf4j // 【已添加】用于日志记录
public class HomeworkService {

    // 【已注入】用于JSON序列化
    private final ObjectMapper objectMapper;

    private final PythonAIServiceClient aiServiceClient;
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
     * 提交作业 (整合了AI分析触发逻辑)
     */
    @Transactional
    public Long submitHomework(Long homeworkId, Long studentId, String content, List<String> fileUrls) {
        Homework homework = getHomeworkById(homeworkId);

        // 检查是否已提交
        HomeworkSubmission existing = homeworkSubmissionMapper.selectByHomeworkAndStudent(homeworkId, studentId);

        // 判断提交状态
        int status = 1; // 准时提交
//        if (LocalDateTime.now().isAfter(homework.getDueDate())) {
//            if (homework.getAllowLateSubmission() == 1) {
//                status = 2; // 迟交
//            } else {
//                throw new BusinessException("作业已截止，不允许提交");
//            }
//        }

        Long submissionId;

        if (existing != null) {
            // 更新提交
            existing.setContent(content);
            existing.setStatus(status);
            existing.setSubmittedAt(LocalDateTime.now());
            homeworkSubmissionMapper.update(existing);

            // 删除旧附件
            submissionFileMapper.deleteBySubmissionId(existing.getSubmissionId());

            submissionId = existing.getSubmissionId();
        } else {
            // 新建提交
            HomeworkSubmission submission = new HomeworkSubmission();
            submission.setHomeworkId(homeworkId);
            submission.setStudentId(studentId);
            submission.setContent(content);
            submission.setStatus(status);
            submission.setSubmittedAt(LocalDateTime.now());
            homeworkSubmissionMapper.insert(submission);

            submissionId = submission.getSubmissionId();
        }

        // 插入附件 (无论是新增还是更新都需要处理附件)
        if (fileUrls != null && !fileUrls.isEmpty()) {
            for (String fileUrl : fileUrls) {
                SubmissionFile file = new SubmissionFile();
                file.setSubmissionId(submissionId);
                file.setFileUrl(fileUrl);
                file.setFileName(fileUrl.substring(fileUrl.lastIndexOf("/") + 1));
                submissionFileMapper.insert(file);
            }
        }

        // ==========================================================
        // 【AI 分析逻辑集成】
        // ==========================================================

        // 1. 创建AI分析任务记录
        SubmissionAnalysis analysis = new SubmissionAnalysis();
        analysis.setSubmissionId(submissionId);
        analysis.setAnalysisStatus(1); // 1:待分析
        submissionAnalysisMapper.insert(analysis);

        // 2. 异步调用AI服务进行分析
        final Long finalSubmissionId = submissionId; // 确保在lambda中使用的变量是final或等效final

        CompletableFuture.runAsync(() -> {
            try {
                // 再次获取作业详情确保数据最新 (或者直接使用方法参数中的 homework)
                // 这里使用方法参数中的 content 即可
                Map<String, Object> requestData = new HashMap<>();
                requestData.put("homework_title", homework.getTitle());
                requestData.put("homework_description", homework.getDescription());
                requestData.put("student_content", content);

                Map<String, Object> aiResult = aiServiceClient.analyzeStudentSubmission(requestData);

                // 更新分析结果
                SubmissionAnalysis updateAnalysis = new SubmissionAnalysis();
                updateAnalysis.setAnalysisId(analysis.getAnalysisId());
                // 【注意】try-catch (Exception e) 已经保护了这里的 JsonProcessingException
                updateAnalysis.setStudentAcReport(objectMapper.writeValueAsString(aiResult));
                updateAnalysis.setAnalysisStatus(3); // 3:已完成
                updateAnalysis.setGeneratedAt(LocalDateTime.now());
                submissionAnalysisMapper.update(updateAnalysis);
                log.info("Submission AI analysis completed successfully for ID: {}", finalSubmissionId);
            } catch (Exception e) {
                log.error("AI作业分析失败, Submission ID: {}", finalSubmissionId, e);
                // 更新状态为失败
                SubmissionAnalysis updateAnalysis = new SubmissionAnalysis();
                updateAnalysis.setAnalysisId(analysis.getAnalysisId());
                updateAnalysis.setAnalysisStatus(4); // 4:失败
                submissionAnalysisMapper.update(updateAnalysis);
            }
        });

        return submissionId;
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

    /**
     * 生成教学建议 (处理 JSON 序列化异常)
     */
    public void generateTeachingSuggestions(Long homeworkId) {
        Homework homework = getHomeworkById(homeworkId);

        // 获取部分学生提交样本
        List<HomeworkSubmission> submissions = homeworkSubmissionMapper.selectByHomeworkId(homeworkId, null);
        List<Map<String, Object>> submissionDataList = new ArrayList<>();

        for (HomeworkSubmission sub : submissions) {
            Map<String, Object> data = new HashMap<>();
            data.put("content", sub.getContent());
            data.put("grade", sub.getGrade());
            data.put("status", sub.getStatus());
            submissionDataList.add(data);

            if (submissionDataList.size() >= 10) break; // 最多取10个样本
        }

        // 调用AI服务生成教学建议
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("homework_title", homework.getTitle());
        requestData.put("homework_description", homework.getDescription());
        requestData.put("student_submissions", submissionDataList);

       // Map<String, Object> aiResponse = aiServiceClient.generateTeachingSuggestions(requestData);
        //List<Map<String, Object>> suggestions = (List<Map<String, Object>>) aiResponse.get("data"); // 假设客户端返回 Map，且实际结果在 "data" 字段
        List<Map<String, Object>> suggestions =  aiServiceClient.generateTeachingSuggestions(requestData);

        if (suggestions != null && !suggestions.isEmpty()) {
            // 保存教学建议
//            for (Map<String, Object> suggestion : suggestions) {
//                TeachingSuggestion teachingSuggestion = new TeachingSuggestion();
//                teachingSuggestion.setClassId(homework.getClassId());
//                teachingSuggestion.setHomeworkId(homeworkId);
            //一次性序列号整个列表
            String fullSuggestionJson;
            try {
                // 将 List<Map<String, Object>> 整个序列化
                fullSuggestionJson = objectMapper.writeValueAsString(suggestions);
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize full teaching suggestion for homeworkId: {}", homeworkId, e);
                throw new BusinessException("保存教学建议时JSON序列化失败: " + e.getMessage());
            }

                //
//                try {
//                    teachingSuggestion.setSuggestionContent(objectMapper.writeValueAsString(suggestion));
//                } catch (JsonProcessingException e) {
//                    log.error("Failed to serialize teaching suggestion for homeworkId: {}", homeworkId, e);
//                    // 抛出业务异常，中止当前事务，防止脏数据
//                    throw new BusinessException("保存教学建议时JSON序列化失败: " + e.getMessage());
//                }
            TeachingSuggestion teachingSuggestion = new TeachingSuggestion();
            teachingSuggestion.setClassId(homework.getClassId());
            teachingSuggestion.setHomeworkId(homeworkId);

            teachingSuggestion.setSuggestionContent(fullSuggestionJson);

            teachingSuggestion.setGeneratedAt(LocalDateTime.now());
                teachingSuggestionMapper.insert(teachingSuggestion);
            log.info("Successfully generated and stored single consolidated teaching suggestion for homeworkId: {}", homeworkId);

        }
         else {
            log.warn("AI service returned null or unexpected format for teaching suggestions for homeworkId: {}", homeworkId);
        }
    }
}