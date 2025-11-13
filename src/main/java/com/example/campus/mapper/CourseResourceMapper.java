package com.example.campus.mapper;

import com.example.campus.entity.CourseResource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程资源Mapper接口
 */
@Mapper
public interface CourseResourceMapper {
    CourseResource selectById(@Param("resourceId") Long resourceId);
    List<CourseResource> selectByClassId(@Param("classId") Long classId, @Param("folderId") Long folderId);
    int insert(CourseResource resource);
    int deleteById(@Param("resourceId") Long resourceId);
    int updateDownloadCount(@Param("resourceId") Long resourceId);
}

