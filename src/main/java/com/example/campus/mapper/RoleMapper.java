package com.example.campus.mapper;

import com.example.campus.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色Mapper接口
 */
@Mapper
public interface RoleMapper {
    Role selectById(@Param("roleId") Integer roleId);
    Role selectByRoleKey(@Param("roleKey") String roleKey);
    List<Role> selectAll();
    List<Role> selectByCondition(@Param("roleName") String roleName);
    int insert(Role role);
    int update(Role role);
    int deleteById(@Param("roleId") Integer roleId);
    List<java.util.Map<String, Object>> selectSimpleList();
}

