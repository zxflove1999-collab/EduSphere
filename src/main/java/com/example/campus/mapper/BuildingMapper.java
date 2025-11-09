package com.example.campus.mapper;

import com.example.campus.entity.Building;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 教学楼Mapper接口
 */
@Mapper
public interface BuildingMapper {
    Building selectById(@Param("buildingId") Integer buildingId);
    List<Building> selectByCondition(@Param("buildingName") String buildingName, @Param("isActive") Integer isActive);
    int insert(Building building);
    int update(Building building);
    int deleteById(@Param("buildingId") Integer buildingId);
}

