package com.example.campus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 宿舍批量分配请求
 */
@Data
public class DormBatchAllocationRequest {
    private List<AllocationItem> allocations;

    @Data
    public static class AllocationItem {
        @JsonProperty("student_id")
        private Long studentId;
        @JsonProperty("bed_id")
        private Long bedId;
        @JsonProperty("check_in_date")
        private LocalDate checkInDate;
    }
}

