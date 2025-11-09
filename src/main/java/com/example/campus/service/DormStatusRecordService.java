package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.common.PageResult;
import com.example.campus.entity.DormStatusRecord;
import com.example.campus.mapper.DormStatusRecordMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 宿舍状态记录服务
 */
@Service
@RequiredArgsConstructor
public class DormStatusRecordService {
    private final DormStatusRecordMapper dormStatusRecordMapper;

    public PageResult<DormStatusRecord> listRecords(Integer page, Integer pageSize, Long studentId, Integer statusType) {
        PageHelper.startPage(page != null ? page : 1, pageSize != null ? pageSize : 10);
        List<DormStatusRecord> records = dormStatusRecordMapper.selectByStudentId(studentId, statusType);
        PageInfo<DormStatusRecord> pageInfo = new PageInfo<>(records);
        return new PageResult<>(pageInfo.getTotal(), records);
    }

    public List<DormStatusRecord> getRecordsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return dormStatusRecordMapper.selectByTimeRange(startTime, endTime);
    }

    @Transactional
    public void createRecord(DormStatusRecord record) {
        if (record.getStartTime() == null) {
            record.setStartTime(LocalDateTime.now());
        }
        dormStatusRecordMapper.insert(record);
    }

    @Transactional
    public void updateRecord(DormStatusRecord record) {
        DormStatusRecord existing = dormStatusRecordMapper.selectById(record.getRecordId());
        if (existing == null) {
            throw new BusinessException("状态记录不存在");
        }
        dormStatusRecordMapper.update(record);
    }

    @Transactional
    public void endRecord(Long recordId, LocalDateTime endTime) {
        DormStatusRecord record = dormStatusRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("状态记录不存在");
        }
        record.setEndTime(endTime);
        dormStatusRecordMapper.update(record);
    }
}

