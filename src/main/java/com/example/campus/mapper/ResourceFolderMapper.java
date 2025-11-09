package com.example.campus.mapper;

import com.example.campus.entity.ResourceFolder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 资源文件夹Mapper接口
 */
@Mapper
public interface ResourceFolderMapper {
    ResourceFolder selectById(@Param("folderId") Long folderId);
    List<ResourceFolder> selectByClassId(@Param("classId") Long classId, @Param("parentFolderId") Long parentFolderId);
    int insert(ResourceFolder folder);
    int deleteById(@Param("folderId") Long folderId);
    int countByParentFolderId(@Param("parentFolderId") Long parentFolderId);
}

