package com.example.campus.controller;

import com.example.campus.service.AutoDormAllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Automatic dorm allocation API
 */
@RestController
@RequestMapping("/api/dorm/allocation")
@RequiredArgsConstructor
public class AutoDormAllocationController {

    private final AutoDormAllocationService service;

    @PostMapping("/run")
    public List<Map<String, Object>> run() {
        return service.runAllocation();
    }
}
