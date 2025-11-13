package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.common.PageResult;
import com.example.campus.entity.Building;
import com.example.campus.mapper.BuildingMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 教学楼管理服务
 */
@Service
@RequiredArgsConstructor
public class BuildingService {
    private final BuildingMapper buildingMapper;

    /**
     * 教学楼列表查询
     */
    public PageResult<Building> listBuildings(Integer page, Integer pageSize, String buildingName, Integer isActive) {
        PageHelper.startPage(page != null ? page : 1, pageSize != null ? pageSize : 10);
        List<Building> buildings = buildingMapper.selectByCondition(buildingName, isActive);
        PageInfo<Building> pageInfo = new PageInfo<>(buildings);
        return new PageResult<>(pageInfo.getTotal(), buildings);
    }

    /**
     * 添加教学楼
     */
    @Transactional
    public void addBuilding(Building building) {
        buildingMapper.insert(building);
    }

    /**
     * 修改教学楼
     */
    @Transactional
    public void updateBuilding(Building building) {
        Building existing = buildingMapper.selectById(building.getBuildingId());
        if (existing == null) {
            throw new BusinessException("教学楼不存在");
        }
        buildingMapper.update(building);
    }

    /**
     * 删除教学楼
     */
    @Transactional
    public void deleteBuilding(Integer buildingId) {
        Building existing = buildingMapper.selectById(buildingId);
        if (existing == null) {
            throw new BusinessException("教学楼不存在");
        }
        buildingMapper.deleteById(buildingId);
    }
}

