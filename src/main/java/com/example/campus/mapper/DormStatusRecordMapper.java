package com.example.campus.mapper;

import com.example.campus.entity.DormStatusRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 宿舍状态记录Mapper接口
 */
@Mapper
public interface DormStatusRecordMapper {
    DormStatusRecord selectById(@Param("recordId") Long recordId);
    List<DormStatusRecord> selectByStudentId(@Param("studentId") Long studentId,
                                              @Param("statusType") Integer statusType);
    List<DormStatusRecord> selectByTimeRange(@Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime);
    int insert(DormStatusRecord record);
    int update(DormStatusRecord record);
    int deleteById(@Param("recordId") Long recordId);
}

