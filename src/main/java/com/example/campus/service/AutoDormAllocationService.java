package com.example.campus.service;

import com.example.campus.entity.DormBed;
import com.example.campus.entity.DormRoom;
import com.example.campus.entity.DormPreference;
import com.example.campus.mapper.DormBedMapper;
import com.example.campus.mapper.DormRoomMapper;
import com.example.campus.mapper.DormPreferenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Automatic dorm allocation service (AI + preferences)
 */
@Service
@RequiredArgsConstructor
public class AutoDormAllocationService {

    private final DormRoomMapper roomMapper;
    private final DormBedMapper bedMapper;
    private final DormPreferenceMapper preferenceMapper;

    /**
     * Run automatic allocation for all students based on preferences.
     */
    public List<Map<String, Object>> runAllocation() {

        // 1. Load all preference records
        List<DormPreference> prefs = preferenceMapper.selectAll();
        if (prefs == null || prefs.isEmpty()) return List.of();

        // 2. Sort by matching score (high → low)
        prefs.sort(Comparator.comparing(DormPreference::getMatchingScore).reversed());

        // 3. Group students into groups of 4
        List<List<DormPreference>> groups = clusterIntoGroups(prefs, 4);

        // 4. Load available rooms (null, null → load all available rooms)
        List<DormRoom> rooms = roomMapper.selectAvailableRooms(null, null);
        int roomIndex = 0;

        List<Map<String, Object>> result = new ArrayList<>();

        for (List<DormPreference> group : groups) {

            if (roomIndex >= rooms.size())
                break;

            DormRoom room = rooms.get(roomIndex++);

            // ★ Change here — use selectAvailableBeds()
            List<DormBed> freeBeds = bedMapper.selectAvailableBeds(room.getRoomId());

            for (int i = 0; i < group.size() && i < freeBeds.size(); i++) {
                DormPreference student = group.get(i);
                DormBed bed = freeBeds.get(i);

                bed.setBedStatus(2);
                bed.setCurrentResidentId(student.getStudentId());
                bed.setAllocationTime(LocalDateTime.now());
                bedMapper.update(bed);
            }

            Map<String, Object> record = new HashMap<>();
            record.put("room", room.getFullName());
            record.put("students", group);
            result.add(record);
        }

        return result;
    }

    private List<List<DormPreference>> clusterIntoGroups(List<DormPreference> list, int size) {
        List<List<DormPreference>> output = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            output.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return output;
    }
}
