package com.example.campus.mapper;

import com.example.campus.entity.TeachingSuggestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI教学建议Mapper接口
 */
@Mapper
public interface TeachingSuggestionMapper {
    List<TeachingSuggestion> selectByHomeworkId(@Param("homeworkId") Long homeworkId);
    List<TeachingSuggestion> selectByClassId(@Param("classId") Long classId);
    int insert(TeachingSuggestion suggestion);
}

