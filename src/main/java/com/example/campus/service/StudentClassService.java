package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.common.PageResult;
import com.example.campus.entity.Class;
import com.example.campus.entity.ClassSchedule;
import com.example.campus.entity.StudentClass;
import com.example.campus.mapper.ClassMapper;
import com.example.campus.mapper.ClassScheduleMapper;
import com.example.campus.mapper.StudentClassMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 学生选课服务
 */
@Service
@RequiredArgsConstructor
public class StudentClassService {
    private final StudentClassMapper studentClassMapper;
    private final ClassMapper classMapper;
    private final ClassScheduleMapper classScheduleMapper;

    /**
     * 可选班级列表查询
     */
    public PageResult<Class> listAvailableClasses(Integer page, Integer pageSize, String courseName,
                                                   String teacherName, Integer departmentId) {
        PageHelper.startPage(page != null ? page : 1, pageSize != null ? pageSize : 10);
        List<Class> classes = classMapper.selectAvailableClasses(courseName, teacherName, departmentId);
        PageInfo<Class> pageInfo = new PageInfo<>(classes);
        return new PageResult<>(pageInfo.getTotal(), classes);
    }

    /**
     * 学生选择课程
     */
    @Transactional
    public void selectClass(Long studentId, Long classId) {
        // 检查班级是否存在且状态为选课中
        Class clazz = classMapper.selectById(classId);
        if (clazz == null) {
            throw new BusinessException("班级不存在");
        }
        if (clazz.getStatus() != 2) {
            throw new BusinessException("该班级不在选课中");
        }
        // 检查是否已选
        StudentClass existing = studentClassMapper.selectByStudentAndClass(studentId, classId);
        if (existing != null && existing.getStatus() == 1) {
            throw new BusinessException("您已选择该课程");
        }
        // 检查容量
        if (clazz.getCurrentEnrollment() >= clazz.getMaxCapacity()) {
            throw new BusinessException("该班级已满");
        }
        
        if (existing != null) {
            // 重新选课
            existing.setStatus(1);
            studentClassMapper.update(existing);
        } else {
            // 新建选课记录
            StudentClass studentClass = new StudentClass();
            studentClass.setStudentId(studentId);
            studentClass.setClassId(classId);
            studentClass.setStatus(1);
            studentClassMapper.insert(studentClass);
        }
        
        // 更新选课人数
        classMapper.updateCurrentEnrollment(classId, 1);
    }

    /**
     * 学生删除课程 (退课)
     */
    @Transactional
    public void dropClass(Long studentId, Long classId) {
        StudentClass studentClass = studentClassMapper.selectByStudentAndClass(studentId, classId);
        if (studentClass == null || studentClass.getStatus() != 1) {
            throw new BusinessException("您未选择该课程");
        }
        studentClass.setStatus(2);
        studentClassMapper.updateStatus(studentClass.getId(), 2);
        
        // 更新选课人数
        classMapper.updateCurrentEnrollment(classId, -1);
    }

    /**
     * 学生已选课程列表
     */
    public List<StudentClass> listMyClasses(Long studentId, String semester) {
        return studentClassMapper.selectByStudentId(studentId, semester);
    }

    /**
     * 学生查询课表
     */
    public List<ClassSchedule> getMySchedule(Long studentId, String semester) {
        return classScheduleMapper.selectByStudentId(studentId, semester);
    }
}

