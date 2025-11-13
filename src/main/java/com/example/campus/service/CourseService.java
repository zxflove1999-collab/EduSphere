package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.common.PageResult;
import com.example.campus.entity.Course;
import com.example.campus.mapper.CourseMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 课程模板管理服务
 */
@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseMapper courseMapper;

    /**
     * 课程模板列表查询
     */
    public PageResult<Course> listCourses(Integer page, Integer pageSize, String courseName,
                                         String courseCode, Integer departmentId) {
        PageHelper.startPage(page != null ? page : 1, pageSize != null ? pageSize : 10);
        List<Course> courses = courseMapper.selectByCondition(courseName, courseCode, departmentId);
        PageInfo<Course> pageInfo = new PageInfo<>(courses);
        return new PageResult<>(pageInfo.getTotal(), courses);
    }

    /**
     * 添加课程模板
     */
    @Transactional
    public void addCourse(Course course, Long createdBy) {
        // 检查课程代码是否已存在
        Course existing = courseMapper.selectByCourseCode(course.getCourseCode());
        if (existing != null) {
            throw new BusinessException("课程代码已存在");
        }
        course.setCreatedBy(createdBy);
        courseMapper.insert(course);
    }

    /**
     * 根据ID查询课程模板
     */
    public Course getCourseById(Long courseId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        return course;
    }

    /**
     * 修改课程模板
     */
    @Transactional
    public void updateCourse(Course course) {
        Course existing = courseMapper.selectById(course.getCourseId());
        if (existing == null) {
            throw new BusinessException("课程不存在");
        }
        // 如果修改了课程代码，检查是否重复
        if (!existing.getCourseCode().equals(course.getCourseCode())) {
            Course codeExists = courseMapper.selectByCourseCode(course.getCourseCode());
            if (codeExists != null) {
                throw new BusinessException("课程代码已存在");
            }
        }
        courseMapper.update(course);
    }

    /**
     * 删除课程模板
     */
    @Transactional
    public void deleteCourse(Long courseId) {
        Course existing = courseMapper.selectById(courseId);
        if (existing == null) {
            throw new BusinessException("课程不存在");
        }
        // 检查是否有班级关联
        int count = courseMapper.countByCourseId(courseId);
        if (count > 0) {
            throw new BusinessException("该课程已有班级关联，无法删除");
        }
        courseMapper.deleteById(courseId);
    }

    /**
     * 查询所有课程模板 (下拉列表)
     */
    public List<Map<String, Object>> listAllCourses() {
        return courseMapper.selectSimpleList();
    }
}

