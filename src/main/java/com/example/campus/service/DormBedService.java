package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.entity.DormBed;
import com.example.campus.entity.DormRoom;
import com.example.campus.mapper.DormBedMapper;
import com.example.campus.mapper.DormRoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 宿舍床位管理服务
 */
@Service
@RequiredArgsConstructor
public class DormBedService {
    private final DormBedMapper dormBedMapper;
    private final DormRoomMapper dormRoomMapper;

    public List<DormBed> getBedsByRoom(Long roomId) {
        return dormBedMapper.selectByRoomId(roomId);
    }

    public List<DormBed> getAvailableBeds(Long roomId) {
        return dormBedMapper.selectAvailableBeds(roomId);
    }

    public DormBed getBedByResident(Long residentId) {
        return dormBedMapper.selectByResidentId(residentId);
    }

    @Transactional
    public void allocateBed(Long bedId, Long studentId, LocalDate checkInDate) {
        DormBed bed = dormBedMapper.selectById(bedId);
        if (bed == null) {
            throw new BusinessException("床位不存在");
        }
        if (bed.getBedStatus() != 1) {
            throw new BusinessException("床位不可用");
        }
        DormBed existing = dormBedMapper.selectByResidentId(studentId);
        if (existing != null) {
            throw new BusinessException("该学生已有床位分配");
        }
        LocalDateTime allocationTime = checkInDate != null ? checkInDate.atStartOfDay() : LocalDateTime.now();
        dormBedMapper.updateStatus(bedId, 2, studentId, allocationTime);
        DormRoom room = dormRoomMapper.selectById(bed.getRoomId());
        if (room != null) {
            int occupiedBeds = room.getOccupiedBeds() + 1;
            dormRoomMapper.updateOccupiedBeds(bed.getRoomId(), occupiedBeds);
            if (occupiedBeds >= room.getTotalBeds()) {
                dormRoomMapper.updateStatus(bed.getRoomId(), 3); // 已满
            }
        }
    }

    @Transactional
    public void releaseBed(Long bedId) {
        DormBed bed = dormBedMapper.selectById(bedId);
        if (bed == null) {
            throw new BusinessException("床位不存在");
        }
        if (bed.getBedStatus() != 2) {
            throw new BusinessException("该床位未分配");
        }
        dormBedMapper.updateStatus(bedId, 1, null, null);
        DormRoom room = dormRoomMapper.selectById(bed.getRoomId());
        if (room != null) {
            int occupiedBeds = Math.max(0, room.getOccupiedBeds() - 1);
            dormRoomMapper.updateOccupiedBeds(bed.getRoomId(), occupiedBeds);
            if (room.getRoomStatus() == 3 && occupiedBeds < room.getTotalBeds()) {
                dormRoomMapper.updateStatus(bed.getRoomId(), 1); // 恢复正常
            }
        }
    }

    @Transactional
    public void saveBed(DormBed bed) {
        if (bed.getBedId() != null) {
            DormBed existing = dormBedMapper.selectById(bed.getBedId());
            if (existing == null) {
                throw new BusinessException("床位不存在");
            }
            dormBedMapper.update(bed);
        } else {
            if (bed.getBedStatus() == null) {
                bed.setBedStatus(1); // 默认空闲
            }
            dormBedMapper.insert(bed);
        }
    }
}

