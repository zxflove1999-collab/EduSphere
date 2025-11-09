package com.example.campus.vo;

import lombok.Data;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 教室信息VO类
 */
@Data
public class ClassroomVO {
    private Long roomId;
    private String fullName;
    private String buildingName;
    private Integer capacity;
    private String roomType;
    private List<String> equipment; // 数组格式
    private Integer isAvailable;
    private LocalDate lastMaintenanceDate;

    /**
     * 从Classroom实体转换为VO
     */
    public static ClassroomVO fromEntity(com.example.campus.entity.Classroom classroom, String buildingName) {
        ClassroomVO vo = new ClassroomVO();
        vo.setRoomId(classroom.getRoomId());
        vo.setFullName(classroom.getFullName());
        vo.setBuildingName(buildingName);
        vo.setCapacity(classroom.getCapacity());
        vo.setRoomType(classroom.getRoomType());
        // 将equipment字符串转换为数组
        if (classroom.getEquipment() != null && !classroom.getEquipment().isEmpty()) {
            vo.setEquipment(Arrays.stream(classroom.getEquipment().split(","))
                .map(String::trim)
                .collect(Collectors.toList()));
        } else {
            vo.setEquipment(List.of());
        }
        vo.setIsAvailable(classroom.getIsAvailable());
        vo.setLastMaintenanceDate(classroom.getLastMaintenanceDate());
        return vo;
    }
}

