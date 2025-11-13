package com.example.campus.mapper;

import com.example.campus.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户角色关联Mapper接口
 */
@Mapper
public interface UserRoleMapper {
    List<Integer> selectRoleIdsByUserId(@Param("userId") Long userId);
    List<String> selectRoleKeysByUserId(@Param("userId") Long userId);
    int insert(UserRole userRole);
    int deleteByUserId(@Param("userId") Long userId);
    int deleteByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Integer roleId);
}

