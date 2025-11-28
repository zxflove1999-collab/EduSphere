package com.example.campus.controller;

import com.example.campus.entity.DormQuestionnaire;
import com.example.campus.service.DormQuestionnaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dorm/questionnaire")
@RequiredArgsConstructor
public class DormQuestionnaireController {

    private final DormQuestionnaireService service;

    /**
     * 学生提交宿舍问卷接口
     */
    @PostMapping("/submit")
    public String submit(@RequestBody DormQuestionnaire q) {
        service.submitQuestionnaire(q);
        return "success";
    }
}
