package com.example.campus.mapper;

import com.example.campus.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {
    User selectByUsername(@Param("username") String username);
    User selectById(@Param("userId") Long userId);
    int insert(User user);
    int update(User user);
    int updatePassword(@Param("userId") Long userId, @Param("passwordHash") String passwordHash);
    int updateTokenVersion(@Param("userId") Long userId);
    List<User> selectByIds(@Param("userIds") List<Long> userIds);
    int deleteById(@Param("userId") Long userId);
}

