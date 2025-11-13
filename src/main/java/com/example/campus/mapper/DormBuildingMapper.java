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
    List<DormBuilding> selectByCondition(@Param("genderLimit") Integer genderLimit,
                                         @Param("isActive") Integer isActive);
    int insert(DormBuilding building);
    int update(DormBuilding building);
    int updateStatus(@Param("buildingId") Integer buildingId, @Param("isActive") Integer isActive);
    int deleteById(@Param("buildingId") Integer buildingId);
}

