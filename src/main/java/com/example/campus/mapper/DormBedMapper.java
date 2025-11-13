package com.example.campus.mapper;

import com.example.campus.entity.DormBed;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 宿舍床位Mapper接口
 */
@Mapper
public interface DormBedMapper {
    DormBed selectById(@Param("bedId") Long bedId);
    List<DormBed> selectByRoomId(@Param("roomId") Long roomId);
    List<DormBed> selectAvailableBeds(@Param("roomId") Long roomId);
    DormBed selectByResidentId(@Param("residentId") Long residentId);
    int insert(DormBed bed);
    int update(DormBed bed);
    int updateStatus(@Param("bedId") Long bedId,
                     @Param("bedStatus") Integer bedStatus,
                     @Param("residentId") Long residentId,
                     @Param("allocationTime") java.time.LocalDateTime allocationTime);
    int deleteById(@Param("bedId") Long bedId);
    List<java.util.Map<String, Object>> selectGlobalAvailableBeds(@Param("buildingId") Integer buildingId);
    List<Long> selectStudentsWithoutBed(@Param("limit") Integer limit);
}

