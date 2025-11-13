package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.common.PageResult;
import com.example.campus.entity.AttendanceRecord;
import com.example.campus.entity.AttendanceSession;
import com.example.campus.mapper.AttendanceRecordMapper;
import com.example.campus.mapper.AttendanceSessionMapper;
import com.example.campus.mapper.ClassMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 考勤管理服务
 */
@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceSessionMapper attendanceSessionMapper;
    private final AttendanceRecordMapper attendanceRecordMapper;
    private final ClassMapper classMapper;

    /**
     * 发起签到
     */
    @Transactional
    public AttendanceSession startAttendance(Long classId, Integer sessionType, Integer duration,
                                             BigDecimal requiredLatitude, BigDecimal requiredLongitude,
                                             Integer requiredRadius, Long createdBy) {
        // 检查班级是否存在
        if (classMapper.selectById(classId) == null) {
            throw new BusinessException("班级不存在");
        }
        
        // 检查是否已有进行中的签到
        AttendanceSession active = attendanceSessionMapper.selectActiveByClassId(classId);
        if (active != null) {
            throw new BusinessException("该班级已有进行中的签到任务");
        }
        
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMinutes(duration);
        
        AttendanceSession session = new AttendanceSession();
        session.setClassId(classId);
        session.setCreatedBy(createdBy);
        session.setSessionType(sessionType);
        session.setStartTime(startTime);
        session.setEndTime(endTime);
        session.setRequiredLatitude(requiredLatitude);
        session.setRequiredLongitude(requiredLongitude);
        session.setRequiredRadius(requiredRadius);
        session.setStatus(1); // 进行中
        
        attendanceSessionMapper.insert(session);
        return session;
    }

    /**
     * 结束签到
     */
    @Transactional
    public void stopAttendance(Long sessionId) {
        AttendanceSession session = attendanceSessionMapper.selectById(sessionId);
        if (session == null) {
            throw new BusinessException("签到任务不存在");
        }
        if (session.getStatus() != 1) {
            throw new BusinessException("签到任务已结束");
        }
        attendanceSessionMapper.updateStatus(sessionId, 2);
    }

    /**
     * 查询班级考勤任务列表
     */
    public PageResult<AttendanceSession> listAttendanceSessions(Long classId, Integer page, Integer pageSize) {
        PageHelper.startPage(page != null ? page : 1, pageSize != null ? pageSize : 10);
        List<AttendanceSession> sessions = attendanceSessionMapper.selectByClassId(classId);
        PageInfo<AttendanceSession> pageInfo = new PageInfo<>(sessions);
        return new PageResult<>(pageInfo.getTotal(), sessions);
    }

    /**
     * 查询考勤详情
     */
    public List<AttendanceRecord> getAttendanceRecords(Long sessionId, Integer status) {
        return attendanceRecordMapper.selectBySessionId(sessionId, status);
    }

    /**
     * 手动修改考勤状态
     */
    @Transactional
    public void updateAttendanceRecord(Long recordId, Integer status, String remarks, Long updatedBy) {
        AttendanceRecord record = attendanceRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("考勤记录不存在");
        }
        record.setStatus(status);
        record.setRemarks(remarks);
        record.setUpdatedBy(updatedBy);
        attendanceRecordMapper.update(record);
    }

    /**
     * 查询当前可签到任务 (学生)
     */
    public AttendanceSession getActiveAttendance(Long classId) {
        return attendanceSessionMapper.selectActiveByClassId(classId);
    }

    /**
     * 课程签到 (学生)
     */
    @Transactional
    public AttendanceRecord checkIn(Long sessionId, Long studentId, BigDecimal checkInLatitude,
                                    BigDecimal checkInLongitude, String faceImageUrl, BigDecimal faceApiScore) {
        AttendanceSession session = attendanceSessionMapper.selectById(sessionId);
        if (session == null) {
            throw new BusinessException("签到任务不存在");
        }
        if (session.getStatus() != 1) {
            throw new BusinessException("签到任务已结束");
        }
        
        // 检查是否已签到
        AttendanceRecord existing = attendanceRecordMapper.selectBySessionAndStudent(sessionId, studentId);
        if (existing != null) {
            throw new BusinessException("您已签到");
        }
        
        // 判断签到状态
        int status = 1; // 出勤
        LocalDateTime checkInTime = LocalDateTime.now();
        
        // 检查是否迟到
        if (checkInTime.isAfter(session.getStartTime().plusMinutes(5))) {
            status = 3; // 迟到
        }
        
        // 验证定位（如果要求）
        if (session.getSessionType() == 2 || session.getSessionType() == 3) {
            if (checkInLatitude == null || checkInLongitude == null) {
                throw new BusinessException("需要提供定位信息");
            }
            // 计算距离（简化处理，实际应该使用地理距离计算）
            // 这里暂时跳过距离验证
        }
        
        // 验证人脸识别（如果要求）
        if (session.getSessionType() == 1 || session.getSessionType() == 3) {
            if (faceImageUrl == null || faceApiScore == null) {
                throw new BusinessException("需要提供人脸识别信息");
            }
            if (faceApiScore.compareTo(new BigDecimal("0.8")) < 0) {
                throw new BusinessException("人脸识别失败，相似度不足");
            }
        }
        
        AttendanceRecord record = new AttendanceRecord();
        record.setSessionId(sessionId);
        record.setStudentId(studentId);
        record.setStatus(status);
        record.setCheckInTime(checkInTime);
        record.setCheckInLatitude(checkInLatitude);
        record.setCheckInLongitude(checkInLongitude);
        record.setFaceApiScore(faceApiScore);
        
        attendanceRecordMapper.insert(record);
        return record;
    }

    /**
     * 查询我的考勤记录 (学生)
     */
    public PageResult<AttendanceRecord> getMyAttendanceRecords(Long studentId, Long classId, Integer page, Integer pageSize) {
        PageHelper.startPage(page != null ? page : 1, pageSize != null ? pageSize : 10);
        List<AttendanceRecord> records = attendanceRecordMapper.selectByStudentIdAndClassId(studentId, classId);
        PageInfo<AttendanceRecord> pageInfo = new PageInfo<>(records);
        return new PageResult<>(pageInfo.getTotal(), records);
    }
}

