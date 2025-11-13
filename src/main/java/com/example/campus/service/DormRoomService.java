package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.common.PageResult;
import com.example.campus.entity.DormRoom;
import com.example.campus.mapper.DormBuildingMapper;
import com.example.campus.mapper.DormRoomMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 宿舍房间管理服务
 */
@Service
@RequiredArgsConstructor
public class DormRoomService {
    private final DormRoomMapper dormRoomMapper;
    private final DormBuildingMapper dormBuildingMapper;

    public PageResult<DormRoom> listRooms(Integer page, Integer pageSize, Integer buildingId, 
                                          String roomNumber, Integer roomStatus) {
        PageHelper.startPage(page != null ? page : 1, pageSize != null ? pageSize : 10);
        List<DormRoom> rooms = dormRoomMapper.selectByCondition(buildingId, roomNumber, roomStatus);
        PageInfo<DormRoom> pageInfo = new PageInfo<>(rooms);
        return new PageResult<>(pageInfo.getTotal(), rooms);
    }

    public List<DormRoom> getAvailableRooms(Integer buildingId, Integer genderLimit) {
        return dormRoomMapper.selectAvailableRooms(buildingId, genderLimit);
    }

    @Transactional
    public void saveRoom(DormRoom room) {
        if (dormBuildingMapper.selectById(room.getBuildingId()) == null) {
            throw new BusinessException("宿舍楼不存在");
        }
        if (room.getFullName() == null || room.getFullName().isEmpty()) {
            com.example.campus.entity.DormBuilding building = dormBuildingMapper.selectById(room.getBuildingId());
            if (building != null) {
                room.setFullName(building.getBuildingName() + room.getRoomNumber());
            } else {
                room.setFullName("宿舍楼" + room.getBuildingId() + "-" + room.getRoomNumber());
            }
        }
        if (room.getRoomId() != null) {
            DormRoom existing = dormRoomMapper.selectById(room.getRoomId());
            if (existing == null) {
                throw new BusinessException("宿舍房间不存在");
            }
            dormRoomMapper.update(room);
        } else {
            if (room.getOccupiedBeds() == null) {
                room.setOccupiedBeds(0);
            }
            dormRoomMapper.insert(room);
        }
    }

    @Transactional
    public void updateRoomStatus(Long roomId, Integer roomStatus) {
        DormRoom existing = dormRoomMapper.selectById(roomId);
        if (existing == null) {
            throw new BusinessException("宿舍房间不存在");
        }
        dormRoomMapper.updateStatus(roomId, roomStatus);
    }

    public DormRoom getRoom(Long roomId) {
        return dormRoomMapper.selectById(roomId);
    }
}

