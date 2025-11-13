package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.common.PageResult;
import com.example.campus.entity.Class;
import com.example.campus.entity.ClassSchedule;
import com.example.campus.mapper.ClassMapper;
import com.example.campus.mapper.ClassScheduleMapper;
import com.example.campus.mapper.CourseMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 开课班级管理服务
 */
@Service
@RequiredArgsConstructor
public class ClassService {
    private final ClassMapper classMapper;
    private final ClassScheduleMapper classScheduleMapper;
    private final CourseMapper courseMapper;

    /**
     * 教师的班级列表查询
     */
    public PageResult<Class> listClassesByTeacher(Long teacherId, Integer page, Integer pageSize,
                                                   String courseName, String semester, Integer status) {
        PageHelper.startPage(page != null ? page : 1, pageSize != null ? pageSize : 10);
        List<Class> classes = classMapper.selectByTeacherId(teacherId, courseName, semester, status);
        PageInfo<Class> pageInfo = new PageInfo<>(classes);
        return new PageResult<>(pageInfo.getTotal(), classes);
    }

    /**
     * 创建班级
     */
    @Transactional
    public void createClass(Class clazz, List<ClassSchedule> schedules, Long teacherId) {
        // 检查课程是否存在
        if (courseMapper.selectById(clazz.getCourseId()) == null) {
            throw new BusinessException("课程不存在");
        }
        clazz.setTeacherId(teacherId);
        clazz.setCurrentEnrollment(0);
        classMapper.insert(clazz);
        
        // 插入课表
        if (schedules != null && !schedules.isEmpty()) {
            for (ClassSchedule schedule : schedules) {
                schedule.setClassId(clazz.getClassId());
                classScheduleMapper.insert(schedule);
            }
        }
    }

    /**
     * 根据ID查询班级详情
     */
    public Map<String, Object> getClassById(Long classId) {
        Class clazz = classMapper.selectById(classId);
        if (clazz == null) {
            throw new BusinessException("班级不存在");
        }
        // 获取课表
        List<ClassSchedule> schedules = classScheduleMapper.selectByClassId(classId);
        
        // 构建返回数据
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("class_id", clazz.getClassId());
        result.put("course_id", clazz.getCourseId());
        result.put("teacher_id", clazz.getTeacherId());
        result.put("semester", clazz.getSemester());
        result.put("max_capacity", clazz.getMaxCapacity());
        result.put("status", clazz.getStatus());
        result.put("schedules", schedules);
        
        // 需要关联查询课程名称和教师名称
        // 这里暂时简化处理
        
        return result;
    }

    /**
     * 修改班级信息
     */
    @Transactional
    public void updateClass(Class clazz, List<ClassSchedule> schedules) {
        Class existing = classMapper.selectById(clazz.getClassId());
        if (existing == null) {
            throw new BusinessException("班级不存在");
        }
        classMapper.update(clazz);
        
        // 删除旧课表，插入新课表
        if (schedules != null) {
            classScheduleMapper.deleteByClassId(clazz.getClassId());
            for (ClassSchedule schedule : schedules) {
                schedule.setClassId(clazz.getClassId());
                classScheduleMapper.insert(schedule);
            }
        }
    }

    /**
     * 删除班级
     */
    @Transactional
    public void deleteClass(Long classId) {
        Class existing = classMapper.selectById(classId);
        if (existing == null) {
            throw new BusinessException("班级不存在");
        }
        // 检查是否有学生选课
        int count = classMapper.countByClassId(classId);
        if (count > 0) {
            throw new BusinessException("该班级已有学生选课，无法删除");
        }
        // 删除课表
        classScheduleMapper.deleteByClassId(classId);
        classMapper.deleteById(classId);
    }

    /**
     * 获取班级的课表
     */
    public List<ClassSchedule> getSchedulesByClassId(Long classId) {
        return classScheduleMapper.selectByClassId(classId);
    }
}

