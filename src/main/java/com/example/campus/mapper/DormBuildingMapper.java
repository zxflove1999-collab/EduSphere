package com.example.campus.mapper;

import com.example.campus.entity.DormBuilding;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 宿舍楼Mapper接口
 */
@Mapper
public interface DormBuildingMapper {
    DormBuilding selectById(@Param("buildingId") Integer buildingId);
    List<DormBuilding> selectByCondition(@Param("buildingName") String buildingName, @Param("isActive") Integer isActive);
    int insert(DormBuilding building);
    int update(DormBuilding building);
    int deleteById(@Param("buildingId") Integer buildingId);
}

