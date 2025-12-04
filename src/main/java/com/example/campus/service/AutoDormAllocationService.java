package com.example.campus.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 自动宿舍分配服务
 * 用于在学生提交问卷后，根据算法自动匹配床位
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AutoDormAllocationService {

    // 这里可以注入其他必要的Service，比如 DormAllocationService
    // private final DormAllocationService dormAllocationService;

    /**
     * 执行自动分配逻辑
     */
    @Transactional
    public void runAllocation() {
        log.info(">>> [自动分配] 开始执行宿舍自动分配算法...");
        
        // TODO: 这里实现具体的分配逻辑
        // 1. 获取未分配的学生
        // 2. 获取空闲床位
        // 3. 根据偏好分数进行匹配
        
        // 模拟耗时操作
        try {
            Thread.sleep(100); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info(">>> [自动分配] 分配算法执行完毕");
    }
}