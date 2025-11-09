package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.entity.CourseResource;
import com.example.campus.entity.ResourceFolder;
import com.example.campus.mapper.ClassMapper;
import com.example.campus.mapper.CourseResourceMapper;
import com.example.campus.mapper.ResourceFolderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程资源管理服务
 */
@Service
@RequiredArgsConstructor
public class ResourceService {
    private final CourseResourceMapper courseResourceMapper;
    private final ResourceFolderMapper resourceFolderMapper;
    private final ClassMapper classMapper;

    /**
     * 查询班级资源列表
     */
    public Map<String, Object> listResources(Long classId, Long folderId) {
        // 检查班级是否存在
        if (classMapper.selectById(classId) == null) {
            throw new BusinessException("班级不存在");
        }
        
        List<ResourceFolder> folders = resourceFolderMapper.selectByClassId(classId, folderId);
        List<CourseResource> files = courseResourceMapper.selectByClassId(classId, folderId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("folders", folders);
        result.put("files", files);
        return result;
    }

    /**
     * 创建资源文件夹
     */
    @Transactional
    public void createFolder(Long classId, String folderName, Long parentFolderId, Long createdBy) {
        // 检查班级是否存在
        if (classMapper.selectById(classId) == null) {
            throw new BusinessException("班级不存在");
        }
        
        ResourceFolder folder = new ResourceFolder();
        folder.setClassId(classId);
        folder.setFolderName(folderName);
        folder.setParentFolderId(parentFolderId);
        folder.setCreatedBy(createdBy);
        resourceFolderMapper.insert(folder);
    }

    /**
     * 上传课程资源
     */
    @Transactional
    public CourseResource uploadResource(Long classId, Long folderId, Long uploaderId,
                                        String fileName, String fileUrl, String fileType,
                                        Long fileSize, String description) {
        // 检查班级是否存在
        if (classMapper.selectById(classId) == null) {
            throw new BusinessException("班级不存在");
        }
        
        CourseResource resource = new CourseResource();
        resource.setClassId(classId);
        resource.setFolderId(folderId);
        resource.setUploaderId(uploaderId);
        resource.setFileName(fileName);
        resource.setFileUrl(fileUrl);
        resource.setFileType(fileType);
        resource.setFileSize(fileSize);
        resource.setDescription(description);
        resource.setDownloadCount(0);
        courseResourceMapper.insert(resource);
        return resource;
    }

    /**
     * 删除课程资源
     */
    @Transactional
    public void deleteResource(Long resourceId) {
        CourseResource resource = courseResourceMapper.selectById(resourceId);
        if (resource == null) {
            throw new BusinessException("资源不存在");
        }
        courseResourceMapper.deleteById(resourceId);
    }

    /**
     * 删除资源文件夹
     */
    @Transactional
    public void deleteFolder(Long folderId) {
        ResourceFolder folder = resourceFolderMapper.selectById(folderId);
        if (folder == null) {
            throw new BusinessException("文件夹不存在");
        }
        // 检查文件夹下是否有子文件夹或文件
        int subFolderCount = resourceFolderMapper.countByParentFolderId(folderId);
        List<CourseResource> files = courseResourceMapper.selectByClassId(folder.getClassId(), folderId);
        if (subFolderCount > 0 || !files.isEmpty()) {
            throw new BusinessException("文件夹不为空，无法删除");
        }
        resourceFolderMapper.deleteById(folderId);
    }

    /**
     * 根据ID查询资源
     */
    public CourseResource getResourceById(Long resourceId) {
        return courseResourceMapper.selectById(resourceId);
    }

    /**
     * 增加下载次数
     */
    public void incrementDownloadCount(Long resourceId) {
        courseResourceMapper.updateDownloadCount(resourceId);
    }
}

