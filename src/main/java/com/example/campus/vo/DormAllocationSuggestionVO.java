package com.example.campus.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 宿舍分配建议视图
 */
@Data
public class DormAllocationSuggestionVO {
    @JsonProperty("total_students_matched")
    private Integer totalStudentsMatched;
    @JsonProperty("suggestions_url")
    private String suggestionsUrl;
    @JsonProperty("preview")
    private List<DormAllocationSuggestionItemVO> preview;
}

