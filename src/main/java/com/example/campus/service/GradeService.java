package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.entity.GradeComponent;
import com.example.campus.entity.StudentGrade;
import com.example.campus.mapper.ClassMapper;
import com.example.campus.mapper.GradeComponentMapper;
import com.example.campus.mapper.StudentClassMapper;
import com.example.campus.mapper.StudentGradeMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 成绩管理服务
 */
@Service
@RequiredArgsConstructor
public class GradeService {
    private final GradeComponentMapper gradeComponentMapper;
    private final StudentGradeMapper studentGradeMapper;
    private final ClassMapper classMapper;
    private final StudentClassMapper studentClassMapper;

    /**
     * 查询班级成绩组成
     */
    public List<GradeComponent> listGradeComponents(Long classId) {
        return gradeComponentMapper.selectByClassId(classId);
    }

    /**
     * 添加成绩组成
     */
    @Transactional
    public void addGradeComponent(GradeComponent component) {
        // 检查班级是否存在
        if (classMapper.selectById(component.getClassId()) == null) {
            throw new BusinessException("班级不存在");
        }
        gradeComponentMapper.insert(component);
    }

    /**
     * 修改成绩组成
     */
    @Transactional
    public void updateGradeComponent(GradeComponent component) {
        GradeComponent existing = gradeComponentMapper.selectById(component.getComponentId());
        if (existing == null) {
            throw new BusinessException("成绩组成项不存在");
        }
        gradeComponentMapper.update(component);
    }

    /**
     * 删除成绩组成
     */
    @Transactional
    public void deleteGradeComponent(Long componentId) {
        GradeComponent existing = gradeComponentMapper.selectById(componentId);
        if (existing == null) {
            throw new BusinessException("成绩组成项不存在");
        }
        // 检查是否有成绩录入
        int count = gradeComponentMapper.countByComponentId(componentId);
        if (count > 0) {
            throw new BusinessException("该成绩组成项已有成绩录入，无法删除");
        }
        gradeComponentMapper.deleteById(componentId);
    }

    /**
     * 查询班级成绩册
     */
    public Map<String, Object> getGradebook(Long classId) {
        // 获取成绩组成
        List<GradeComponent> components = gradeComponentMapper.selectByClassId(classId);
        
        // 获取所有学生成绩
        List<StudentGrade> allGrades = studentGradeMapper.selectByClassId(classId);
        
        // 获取班级所有学生信息
        List<Map<String, Object>> students = studentClassMapper.selectStudentsByClassId(classId);
        
        // 构建成绩册数据
        Map<String, Object> result = new HashMap<>();
        result.put("components", components);
        result.put("students", buildStudentGradeList(classId, components, allGrades, students));
        
        return result;
    }

    /**
     * 批量录入/修改学生成绩
     */
    @Transactional
    public void batchUpdateGrades(Long componentId, List<GradeScore> scores, Long recordedBy) {
        GradeComponent component = gradeComponentMapper.selectById(componentId);
        if (component == null) {
            throw new BusinessException("成绩组成项不存在");
        }
        
        for (GradeScore score : scores) {
            StudentGrade existing = studentGradeMapper.selectByStudentAndComponent(score.getStudent_id(), componentId);
            if (existing != null) {
                existing.setScore(score.getScore());
                existing.setRecordedBy(recordedBy);
                studentGradeMapper.update(existing);
            } else {
                StudentGrade grade = new StudentGrade();
                grade.setStudentId(score.getStudent_id());
                grade.setComponentId(componentId);
                grade.setScore(score.getScore());
                grade.setRecordedBy(recordedBy);
                studentGradeMapper.insert(grade);
            }
        }
        
        // 更新最终成绩
        updateFinalScores(component.getClassId());
    }

    /**
     * 查询我的成绩
     */
    public Map<String, Object> getMyGrades(Long studentId, Long classId) {
        // 获取成绩组成
        List<GradeComponent> components = gradeComponentMapper.selectByClassId(classId);
        
        // 获取学生成绩
        List<StudentGrade> grades = studentGradeMapper.selectByStudentIdAndClassId(studentId, classId);
        
        // 构建成绩数据
        Map<String, Object> result = new HashMap<>();
        result.put("components", buildComponentGradeList(components, grades));
        
        // 计算最终成绩
        BigDecimal finalScore = calculateFinalScore(components, grades);
        result.put("final_score", finalScore);
        
        return result;
    }

    private List<Map<String, Object>> buildComponentGradeList(List<GradeComponent> components, List<StudentGrade> grades) {
        Map<Long, StudentGrade> gradeMap = grades.stream()
            .collect(Collectors.toMap(StudentGrade::getComponentId, g -> g));
        
        return components.stream().map(component -> {
            Map<String, Object> item = new HashMap<>();
            item.put("component_name", component.getComponentName());
            item.put("weight", component.getWeight());
            item.put("max_score", component.getMaxScore());
            
            StudentGrade grade = gradeMap.get(component.getComponentId());
            item.put("my_score", grade != null ? grade.getScore() : null);
            
            return item;
        }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> buildStudentGradeList(Long classId, List<GradeComponent> components, 
                                                           List<StudentGrade> allGrades, 
                                                           List<Map<String, Object>> students) {
        // 构建成绩映射
        Map<Long, Map<Long, BigDecimal>> gradeMap = new HashMap<>();
        for (StudentGrade grade : allGrades) {
            gradeMap.computeIfAbsent(grade.getStudentId(), k -> new HashMap<>())
                    .put(grade.getComponentId(), grade.getScore());
        }
        
        // 构建学生成绩列表
        return students.stream().map(student -> {
            Long studentId = ((Number) student.get("student_id")).longValue();
            Map<String, Object> studentData = new HashMap<>();
            studentData.put("student_id", studentId);
            studentData.put("full_name", student.get("full_name"));
            studentData.put("student_id_number", student.get("student_id_number"));
            
            // 构建成绩
            Map<Long, BigDecimal> scores = gradeMap.getOrDefault(studentId, new HashMap<>());
            Map<String, Object> scoreMap = new HashMap<>();
            for (GradeComponent component : components) {
                scoreMap.put(component.getComponentId().toString(), scores.get(component.getComponentId()));
            }
            studentData.put("scores", scoreMap);
            
            // 计算最终成绩
            BigDecimal finalScore = calculateFinalScoreForStudent(components, scores);
            studentData.put("final_score", finalScore);
            
            return studentData;
        }).collect(Collectors.toList());
    }
    
    private BigDecimal calculateFinalScoreForStudent(List<GradeComponent> components, Map<Long, BigDecimal> scores) {
        BigDecimal totalScore = BigDecimal.ZERO;
        for (GradeComponent component : components) {
            BigDecimal score = scores.get(component.getComponentId());
            if (score != null) {
                totalScore = totalScore.add(score.multiply(component.getWeight()));
            }
        }
        return totalScore;
    }

    private BigDecimal calculateFinalScore(List<GradeComponent> components, List<StudentGrade> grades) {
        Map<Long, StudentGrade> gradeMap = grades.stream()
            .collect(Collectors.toMap(StudentGrade::getComponentId, g -> g));
        
        BigDecimal totalScore = BigDecimal.ZERO;
        for (GradeComponent component : components) {
            StudentGrade grade = gradeMap.get(component.getComponentId());
            if (grade != null && grade.getScore() != null) {
                totalScore = totalScore.add(grade.getScore().multiply(component.getWeight()));
            }
        }
        return totalScore;
    }

    private void updateFinalScores(Long classId) {
        // 获取所有学生
        // 为每个学生计算最终成绩并更新到t_student_class表
        // 这里需要根据实际需求实现
    }

    @Data
    public static class GradeScore {
        private Long student_id;
        private BigDecimal score;
    }
}

