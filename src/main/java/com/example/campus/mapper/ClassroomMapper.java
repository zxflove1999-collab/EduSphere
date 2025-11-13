package com.example.campus.mapper;

import com.example.campus.entity.Classroom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 教室信息Mapper接口
 */
@Mapper
public interface ClassroomMapper {
    Classroom selectById(@Param("roomId") Long roomId);
    List<java.util.Map<String, Object>> selectByCondition(@Param("buildingId") Integer buildingId, 
                                      @Param("roomNumber") String roomNumber, 
                                      @Param("isAvailable") Integer isAvailable);
    List<java.util.Map<String, Object>> selectAvailableRooms(@Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime,
                                         @Param("buildingId") Integer buildingId,
                                         @Param("capacityMin") Integer capacityMin,
                                         @Param("equipmentRequired") String equipmentRequired);
    int insert(Classroom classroom);
    int update(Classroom classroom);
    int updateStatus(@Param("roomId") Long roomId, @Param("isAvailable") Integer isAvailable);
    int deleteById(@Param("roomId") Long roomId);
}

