package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.common.PageResult;
import com.example.campus.entity.DormBuilding;
import com.example.campus.mapper.DormBuildingMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 宿舍楼管理服务
 */
@Service
@RequiredArgsConstructor
public class DormBuildingService {
    private final DormBuildingMapper dormBuildingMapper;

    public PageResult<DormBuilding> listBuildings(Integer page, Integer pageSize, String buildingName, Integer isActive) {
        PageHelper.startPage(page != null ? page : 1, pageSize != null ? pageSize : 10);
        List<DormBuilding> buildings = dormBuildingMapper.selectByCondition(buildingName, isActive);
        PageInfo<DormBuilding> pageInfo = new PageInfo<>(buildings);
        return new PageResult<>(pageInfo.getTotal(), buildings);
    }

    @Transactional
    public void saveBuilding(DormBuilding building) {
        if (building.getBuildingId() != null) {
            DormBuilding existing = dormBuildingMapper.selectById(building.getBuildingId());
            if (existing == null) {
                throw new BusinessException("宿舍楼不存在");
            }
            dormBuildingMapper.update(building);
        } else {
            dormBuildingMapper.insert(building);
        }
    }

    @Transactional
    public void deleteBuilding(Integer buildingId) {
        DormBuilding existing = dormBuildingMapper.selectById(buildingId);
        if (existing == null) {
            throw new BusinessException("宿舍楼不存在");
        }
        dormBuildingMapper.deleteById(buildingId);
    }

    public DormBuilding getBuilding(Integer buildingId) {
        return dormBuildingMapper.selectById(buildingId);
    }
}

