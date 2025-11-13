package com.example.campus.mapper;

import com.example.campus.entity.DormRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 宿舍房间Mapper接口
 */
@Mapper
public interface DormRoomMapper {
    DormRoom selectById(@Param("roomId") Long roomId);
    List<DormRoom> selectByCondition(@Param("buildingId") Integer buildingId, 
                                     @Param("roomNumber") String roomNumber,
                                     @Param("roomStatus") Integer roomStatus);
    List<DormRoom> selectAvailableRooms(@Param("buildingId") Integer buildingId,
                                         @Param("genderLimit") Integer genderLimit);
    int insert(DormRoom room);
    int update(DormRoom room);
    int updateStatus(@Param("roomId") Long roomId, @Param("roomStatus") Integer roomStatus);
    int updateOccupiedBeds(@Param("roomId") Long roomId, @Param("occupiedBeds") Integer occupiedBeds);
    int deleteById(@Param("roomId") Long roomId);
}

