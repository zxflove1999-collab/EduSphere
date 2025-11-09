package com.example.campus.entity;

import lombok.Data;
import java.time.LocalTime;

/**
 * 课表时间地点实体类
 */
@Data
public class ClassSchedule {
    private Long scheduleId;
    private Long classId;
    private String locationText;
    private Long classroomId;
    private Integer dayOfWeek; // 1=周一, 7=周日
    private LocalTime startTime;
    private LocalTime endTime;
    private String weekRange; // 如: "1-16" 或 "1,3,5-10"
}

