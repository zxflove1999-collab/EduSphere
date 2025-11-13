package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.common.PageResult;
import com.example.campus.dto.DormRoomSaveRequest;
import com.example.campus.entity.DormBed;
import com.example.campus.entity.DormRoom;
import com.example.campus.mapper.DormBuildingMapper;
import com.example.campus.mapper.DormBedMapper;
import com.example.campus.mapper.DormRoomMapper;
import com.example.campus.vo.DormOccupantVO;
import com.example.campus.vo.DormRoomOverviewVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 宿舍房间管理服务
 */
@Service
@RequiredArgsConstructor
public class DormRoomService {
    private final DormRoomMapper dormRoomMapper;
    private final DormBuildingMapper dormBuildingMapper;
    private final DormBedMapper dormBedMapper;

    public PageResult<DormRoomOverviewVO> listRooms(Integer page, Integer pageSize, Integer buildingId,
                                                    String roomNumber, Integer occupancyStatus) {
        PageHelper.startPage(page != null ? page : 1, pageSize != null ? pageSize : 10);
        List<DormRoom> rooms = dormRoomMapper.selectByCondition(buildingId, roomNumber, occupancyStatus);
        PageInfo<DormRoom> pageInfo = new PageInfo<>(rooms);

        List<DormRoomOverviewVO> roomVOs = buildRoomOverviews(rooms);

        return new PageResult<>(pageInfo.getTotal(), roomVOs);
    }

    public List<DormRoom> getAvailableRooms(Integer buildingId, Integer genderLimit) {
        return dormRoomMapper.selectAvailableRooms(buildingId, genderLimit);
    }

    @Transactional
    public void saveRoom(DormRoomSaveRequest request) {
        if (dormBuildingMapper.selectById(request.getBuildingId()) == null) {
            throw new BusinessException("宿舍楼不存在");
        }
        if (request.getRoomNumber() == null || request.getRoomNumber().isBlank()) {
            throw new BusinessException("房间编号不能为空");
        }
        if (request.getTotalBeds() == null || request.getTotalBeds() <= 0) {
            throw new BusinessException("床位总数必须大于0");
        }

        DormRoom room;
        if (request.getRoomId() != null) {
            room = dormRoomMapper.selectById(request.getRoomId());
            if (room == null) {
                throw new BusinessException("宿舍房间不存在");
            }
            int originalTotalBeds = room.getTotalBeds();
            applyRoomRequest(room, request);
            dormRoomMapper.update(room);
            adjustBeds(room.getRoomId(), originalTotalBeds, room.getTotalBeds());
        } else {
            room = new DormRoom();
            applyRoomRequest(room, request);
            room.setOccupiedBeds(0);
            room.setRoomStatus(1);
            dormRoomMapper.insert(room);
            initializeBeds(room.getRoomId(), room.getTotalBeds());
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

    public DormRoomOverviewVO getRoomOverview(Long roomId) {
        DormRoom room = dormRoomMapper.selectById(roomId);
        if (room == null) {
            return null;
        }
        List<DormRoomOverviewVO> overviews = buildRoomOverviews(List.of(room));
        return overviews.isEmpty() ? null : overviews.get(0);
    }

    private List<DormRoomOverviewVO> buildRoomOverviews(List<DormRoom> rooms) {
        if (rooms == null || rooms.isEmpty()) {
            return List.of();
        }
        List<Long> roomIds = rooms.stream()
                .map(DormRoom::getRoomId)
                .collect(Collectors.toList());

        Map<Long, List<DormOccupantVO>> occupantMap = new HashMap<>();
        if (!roomIds.isEmpty()) {
            List<Map<String, Object>> occupantRecords = dormRoomMapper.selectOccupantsByRoomIds(roomIds);
            for (Map<String, Object> record : occupantRecords) {
                Long roomId = ((Number) record.get("room_id")).longValue();
                DormOccupantVO occupant = new DormOccupantVO();
                occupant.setStudentId(record.get("student_id") != null ? ((Number) record.get("student_id")).longValue() : null);
                occupant.setFullName((String) record.get("full_name"));
                occupant.setPhoneNumber((String) record.get("phone_number"));
                occupant.setBedNumber((String) record.get("bed_number"));
                occupantMap.computeIfAbsent(roomId, k -> new ArrayList<>()).add(occupant);
            }
        }

        List<DormRoomOverviewVO> result = new ArrayList<>();
        for (DormRoom room : rooms) {
            DormRoomOverviewVO vo = new DormRoomOverviewVO();
            vo.setRoomId(room.getRoomId());
            vo.setBuildingId(room.getBuildingId());
            vo.setBuildingName(room.getBuildingName());
            vo.setFullName(room.getFullName());
            vo.setRoomNumber(room.getRoomNumber());
            vo.setTotalBeds(room.getTotalBeds());
            vo.setOccupiedBeds(room.getOccupiedBeds());

            int occupancyStatus;
            if (room.getOccupiedBeds() == null || room.getOccupiedBeds() == 0) {
                occupancyStatus = 1;
            } else if (room.getOccupiedBeds() >= room.getTotalBeds()) {
                occupancyStatus = 3;
            } else {
                occupancyStatus = 2;
            }
            vo.setOccupancyStatus(occupancyStatus);
            vo.setIsFull(occupancyStatus == 3 ? 1 : 0);
            vo.setRoomType(room.getRoomType());
            vo.setHasPrivateBathroom(room.getHasPrivateBathroom());
            vo.setCurrentOccupants(occupantMap.getOrDefault(room.getRoomId(), List.of()));
            result.add(vo);
        }
        return result;
    }

    private void applyRoomRequest(DormRoom room, DormRoomSaveRequest request) {
        room.setRoomId(request.getRoomId());
        room.setBuildingId(request.getBuildingId());
        room.setRoomNumber(request.getRoomNumber());
        room.setTotalBeds(request.getTotalBeds());
        room.setRoomType(request.getRoomType());
        room.setHasPrivateBathroom(request.getHasPrivateBathroom() != null ? request.getHasPrivateBathroom() : 0);

        var building = dormBuildingMapper.selectById(request.getBuildingId());
        if (building != null) {
            room.setFullName(building.getBuildingName() + request.getRoomNumber());
            room.setBuildingName(building.getBuildingName());
        } else {
            room.setFullName("宿舍楼" + request.getBuildingId() + "-" + request.getRoomNumber());
        }
    }

    private void initializeBeds(Long roomId, Integer totalBeds) {
        for (int i = 1; i <= totalBeds; i++) {
            DormBed bed = new DormBed();
            bed.setRoomId(roomId);
            bed.setBedNumber("B" + i);
            bed.setBedStatus(1);
            dormBedMapper.insert(bed);
        }
    }

    private void adjustBeds(Long roomId, int originalTotalBeds, int newTotalBeds) {
        if (newTotalBeds == originalTotalBeds) {
            return;
        }
        List<DormBed> beds = dormBedMapper.selectByRoomId(roomId);
        if (newTotalBeds > originalTotalBeds) {
            for (int i = originalTotalBeds + 1; i <= newTotalBeds; i++) {
                DormBed bed = new DormBed();
                bed.setRoomId(roomId);
                bed.setBedNumber("B" + i);
                bed.setBedStatus(1);
                dormBedMapper.insert(bed);
            }
        } else {
            // new total less than original
            // ensure no occupant resides in beds beyond new total
            List<DormBed> removableBeds = beds.stream()
                    .filter(b -> extractIndex(b.getBedNumber()) > newTotalBeds)
                    .collect(Collectors.toList());
            boolean hasOccupied = removableBeds.stream().anyMatch(b -> b.getBedStatus() != null && b.getBedStatus() == 2);
            if (hasOccupied) {
                throw new BusinessException("存在已入住的床位，无法减少床位数量，请先调整学生宿舍分配");
            }
            for (DormBed bed : removableBeds) {
                dormBedMapper.deleteById(bed.getBedId());
            }
            dormRoomMapper.updateOccupiedBeds(roomId,
                    Math.min(newTotalBeds, beds.stream().mapToInt(b -> b.getBedStatus() != null && b.getBedStatus() == 2 ? 1 : 0).sum()));
        }
    }

    private int extractIndex(String bedNumber) {
        if (bedNumber == null) {
            return Integer.MAX_VALUE;
        }
        try {
            return Integer.parseInt(bedNumber.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException ex) {
            return Integer.MAX_VALUE;
        }
    }
}

