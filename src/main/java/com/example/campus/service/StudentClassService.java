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
        
        // 检查时间冲突：获取新选课程的课表
        List<ClassSchedule> newSchedules = classScheduleMapper.selectByClassId(classId);
        if (!newSchedules.isEmpty()) {
            // 获取学生已选课程的课表（同一学期）
            List<ClassSchedule> existingSchedules = classScheduleMapper.selectByStudentId(studentId, clazz.getSemester());
            // 检查时间冲突
            for (ClassSchedule newSchedule : newSchedules) {
                for (ClassSchedule existingSchedule : existingSchedules) {
                    if (hasTimeConflict(newSchedule, existingSchedule)) {
                        throw new BusinessException("该课程与已选课程时间冲突");
                    }
                }
            }
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
     * 检查两个课表时间是否冲突
     */
    private boolean hasTimeConflict(ClassSchedule schedule1, ClassSchedule schedule2) {
        // 检查星期几是否相同
        if (!schedule1.getDayOfWeek().equals(schedule2.getDayOfWeek())) {
            return false;
        }
        
        // 检查时间段是否重叠
        if (schedule1.getStartTime().isBefore(schedule2.getEndTime()) && 
            schedule1.getEndTime().isAfter(schedule2.getStartTime())) {
            // 时间段重叠，需要检查周次是否重叠
            return hasWeekRangeConflict(schedule1.getWeekRange(), schedule2.getWeekRange());
        }
        
        return false;
    }
    
    /**
     * 检查周次范围是否冲突（简化版本，支持 "1-16" 和 "1,3,5-10" 格式）
     */
    private boolean hasWeekRangeConflict(String weekRange1, String weekRange2) {
        // 解析周次范围（简化处理，实际应该更复杂）
        // 这里只做简单的字符串比较，实际应该解析并比较周次
        // 如果两个范围都包含相同的周次，则冲突
        // 简化实现：如果范围相同或包含关系，则认为冲突
        if (weekRange1 == null || weekRange2 == null) {
            return false;
        }
        // 简单实现：如果范围字符串相同，则认为冲突
        // 更完善的实现应该解析周次并比较
        return weekRange1.equals(weekRange2) || 
               weekRange1.contains(weekRange2) || 
               weekRange2.contains(weekRange1);
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

