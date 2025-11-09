package com.example.campus.controller;

import com.example.campus.common.PageResult;
import com.example.campus.common.Result;
import com.example.campus.entity.TeacherProfile;
import com.example.campus.entity.User;
import com.example.campus.service.TeacherService;
import com.example.campus.vo.TeacherVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 教师管理控制器
 */
@RestController
@RequestMapping("/admin/teachers")
@RequiredArgsConstructor
public class TeacherController {
    private final TeacherService teacherService;

    /**
     * 教师列表查询
     */
    @GetMapping
    public Result<PageResult<TeacherVO>> listTeachers(@RequestParam(required = false) Integer page,
                                                        @RequestParam(required = false) Integer pageSize,
                                                        @RequestParam(required = false) String full_name,
                                                        @RequestParam(required = false) String teacher_id_number,
                                                        @RequestParam(required = false) String department,
                                                        @RequestParam(required = false) String title,
                                                        @RequestParam(required = false) Integer status) {
        PageResult<TeacherVO> result = teacherService.listTeachers(page, pageSize, full_name,
                teacher_id_number, department, title, status);
        return Result.success(result);
    }

    /**
     * 添加教师
     */
    @PostMapping
    public Result<?> addTeacher(@RequestBody TeacherCreateRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(request.getPassword());
        user.setFullName(request.getFull_name());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhone_number());

        TeacherProfile profile = new TeacherProfile();
        profile.setGender(request.getGender());
        profile.setDepartment(request.getDepartment());
        profile.setTitle(request.getTitle());
        profile.setOfficeLocation(request.getOffice_location());

        teacherService.addTeacher(user, profile);
        return Result.success();
    }

    /**
     * 根据ID查询教师
     */
    @GetMapping("/{id}")
    public Result<TeacherVO> getTeacherById(@PathVariable Long id) {
        TeacherVO teacher = teacherService.getTeacherById(id);
        return Result.success(teacher);
    }

    /**
     * 修改教师资料
     */
    @PutMapping
    public Result<?> updateTeacher(@RequestBody TeacherUpdateRequest request) {
        User user = new User();
        user.setUserId(request.getUser_id());
        user.setFullName(request.getFull_name());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhone_number());
        user.setStatus(request.getStatus());

        TeacherProfile profile = new TeacherProfile();
        profile.setDepartment(request.getDepartment());
        profile.setTitle(request.getTitle());
        profile.setOfficeLocation(request.getOffice_location());

        teacherService.updateTeacher(user, profile);
        return Result.success();
    }

    /**
     * 删除教师
     */
    @DeleteMapping("/{ids}")
    public Result<?> deleteTeachers(@PathVariable String ids) {
        List<Long> userIds = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        teacherService.deleteTeachers(userIds);
        return Result.success();
    }

    // 内部类用于接收请求
    @lombok.Data
    static class TeacherCreateRequest {
        private String username;
        private String password;
        private String full_name;
        private String email;
        private String phone_number;
        private Integer gender;
        private String department;
        private String title;
        private String office_location;
    }

    @lombok.Data
    static class TeacherUpdateRequest {
        private Long user_id;
        private String full_name;
        private String email;
        private String phone_number;
        private Integer status;
        private String department;
        private String title;
        private String office_location;
    }
}

