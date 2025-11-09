package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.common.PageResult;
import com.example.campus.entity.Classroom;
import com.example.campus.mapper.BuildingMapper;
import com.example.campus.mapper.ClassroomMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 教室管理服务
 */
@Service
@RequiredArgsConstructor
public class ClassroomService {
    private final ClassroomMapper classroomMapper;
    private final BuildingMapper buildingMapper;

    /**
     * 教室信息列表查询
     */
    public PageResult<java.util.Map<String, Object>> listClassrooms(Integer page, Integer pageSize, Integer buildingId,
                                                String roomNumber, Integer isAvailable) {
        PageHelper.startPage(page != null ? page : 1, pageSize != null ? pageSize : 10);
        List<java.util.Map<String, Object>> classrooms = classroomMapper.selectByCondition(buildingId, roomNumber, isAvailable);
        
        // 转换equipment字段为数组
        for (java.util.Map<String, Object> classroom : classrooms) {
            String equipment = (String) classroom.get("equipment");
            if (equipment != null && !equipment.isEmpty()) {
                classroom.put("equipment", Arrays.asList(equipment.split(",")));
            } else {
                classroom.put("equipment", List.of());
            }
        }
        
        PageInfo<java.util.Map<String, Object>> pageInfo = new PageInfo<>(classrooms);
        return new PageResult<>(pageInfo.getTotal(), classrooms);
    }

    /**
     * 设置教室信息 (新增/修改)
     */
    @Transactional
    public void saveClassroom(Classroom classroom) {
        // 检查教学楼是否存在
        if (buildingMapper.selectById(classroom.getBuildingId()) == null) {
            throw new BusinessException("教学楼不存在");
        }
        
        // 生成教室全称
        if (classroom.getFullName() == null || classroom.getFullName().isEmpty()) {
            com.example.campus.entity.Building building = buildingMapper.selectById(classroom.getBuildingId());
            if (building != null) {
                classroom.setFullName(building.getBuildingName() + classroom.getRoomNumber());
            } else {
                classroom.setFullName("教学楼" + classroom.getBuildingId() + "-" + classroom.getRoomNumber());
            }
        }
        
        if (classroom.getRoomId() != null) {
            // 修改
            Classroom existing = classroomMapper.selectById(classroom.getRoomId());
            if (existing == null) {
                throw new BusinessException("教室不存在");
            }
            classroomMapper.update(classroom);
        } else {
            // 新增
            classroomMapper.insert(classroom);
        }
    }

    /**
     * 设置停用教室
     */
    @Transactional
    public void updateClassroomStatus(Long roomId, Integer isAvailable) {
        Classroom existing = classroomMapper.selectById(roomId);
        if (existing == null) {
            throw new BusinessException("教室不存在");
        }
        classroomMapper.updateStatus(roomId, isAvailable);
    }

    /**
     * 查询可用教室
     */
    public List<Map<String, Object>> getAvailableRooms(LocalDateTime startTime, LocalDateTime endTime,
                                             Integer buildingId, Integer capacityMin, String equipmentRequired) {
        List<Map<String, Object>> rooms = classroomMapper.selectAvailableRooms(startTime, endTime, buildingId, capacityMin, equipmentRequired);
        
        // 转换equipment字段为数组
        for (Map<String, Object> room : rooms) {
            String equipment = (String) room.get("equipment");
            if (equipment != null && !equipment.isEmpty()) {
                room.put("equipment", Arrays.asList(equipment.split(",")));
            } else {
                room.put("equipment", List.of());
            }
        }
        
        return rooms;
    }
}

