package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.dto.DormAllocationSuggestionRequest;
import com.example.campus.dto.DormBatchAllocationRequest;
import com.example.campus.mapper.DormBedMapper;
import com.example.campus.mapper.DormRoomMapper;
import com.example.campus.mapper.DormPreferenceMapper;
import com.example.campus.mapper.UserMapper;
import com.example.campus.vo.DormAllocationSuggestionItemVO;
import com.example.campus.vo.DormAllocationSuggestionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 宿舍分配相关服务
 */
@Service
@RequiredArgsConstructor
public class DormAllocationService {

    private final DormBedService dormBedService;
    private final DormBedMapper dormBedMapper;
    private final DormRoomMapper dormRoomMapper;
    private final DormPreferenceMapper dormPreferenceMapper;
    private final UserMapper userMapper;

    /**
     * 批量分配宿舍
     */
    @Transactional
    public void batchAllocate(DormBatchAllocationRequest request) {
        if (request.getAllocations() == null || request.getAllocations().isEmpty()) {
            throw new BusinessException("分配列表不能为空");
        }
        for (DormBatchAllocationRequest.AllocationItem item : request.getAllocations()) {
            dormBedService.allocateBed(item.getBedId(), item.getStudentId(), item.getCheckInDate());
        }
    }

    /**
     * 生成宿舍分配建议（示例实现）
     */
    public DormAllocationSuggestionVO generateSuggestions(DormAllocationSuggestionRequest request) {
        // 获取可用床位
        List<Map<String, Object>> availableBeds = dormBedMapper.selectGlobalAvailableBeds(request.getTargetDormBuildingId());
        if (availableBeds.isEmpty()) {
            DormAllocationSuggestionVO vo = new DormAllocationSuggestionVO();
            vo.setTotalStudentsMatched(0);
            vo.setSuggestionsUrl(null);
            vo.setPreview(List.of());
            return vo;
        }

        // 简化逻辑：选取前5个可用床位，匹配任意未分配学生
        List<Long> candidateStudentIds = dormBedMapper.selectStudentsWithoutBed(5);
        List<DormAllocationSuggestionItemVO> preview = new ArrayList<>();

        List<Map<String, Object>> limitedBeds = availableBeds.stream().limit(candidateStudentIds.size()).collect(Collectors.toList());
        List<Long> studentIds = candidateStudentIds.stream().limit(limitedBeds.size()).collect(Collectors.toList());
        if (studentIds.isEmpty()) {
            DormAllocationSuggestionVO vo = new DormAllocationSuggestionVO();
            vo.setTotalStudentsMatched(0);
            vo.setSuggestionsUrl(null);
            vo.setPreview(List.of());
            return vo;
        }

        List<Long> bedRoomIds = limitedBeds.stream()
                .map(map -> ((Number) map.get("room_id")).longValue())
                .collect(Collectors.toList());

        Map<Long, String> roomFullNameMap = bedRoomIds.stream()
                .collect(Collectors.toMap(id -> id,
                        id -> {
                            var room = dormRoomMapper.selectById(id);
                            return room != null ? room.getFullName() : "";
                        }));

        Map<Long, String> studentNameMap = studentIds.isEmpty()
                ? Map.of()
                : userMapper.selectByIds(studentIds).stream()
                    .collect(Collectors.toMap(user -> user.getUserId(), user -> user.getFullName()));

        for (int i = 0; i < studentIds.size(); i++) {
            Long studentId = studentIds.get(i);
            Map<String, Object> bedInfo = limitedBeds.get(i);
            DormAllocationSuggestionItemVO item = new DormAllocationSuggestionItemVO();
            item.setStudentId(studentId);
            item.setFullName(studentNameMap.get(studentId));
            item.setSuggestedBedId(((Number) bedInfo.get("bed_id")).longValue());
            Long roomId = ((Number) bedInfo.get("room_id")).longValue();
            item.setSuggestedRoom(roomFullNameMap.get(roomId));
            var preference = dormPreferenceMapper.selectByStudentId(studentId);
            item.setAverageMatchScore(preference != null ? preference.getMatchingScore() : BigDecimal.valueOf(0.75));
            preview.add(item);
        }

        DormAllocationSuggestionVO vo = new DormAllocationSuggestionVO();
        vo.setTotalStudentsMatched(preview.size());
        vo.setSuggestionsUrl("https://example.com/allocation-report/" + UUID.randomUUID());
        vo.setPreview(preview);
        return vo;
    }
}

